package legends.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import legends.Application;
import legends.helper.EventHelper;
import legends.model.basic.AbstractObject;
import legends.model.collections.OccasionCollection;
import legends.model.collections.WarCollection;
import legends.model.events.basic.Coords;
import legends.model.events.basic.Filters;
import legends.xml.annotation.Xml;
import legends.xml.annotation.XmlConverter;
import legends.xml.converter.CoordListConverter;

public class Entity extends AbstractObject {
	@Xml("name")
	private String name;
	@Xml("race")
	private String race = "unknown";
	@Xml("type")
	private String type = "unknown";
	private Set<Site> sites = new LinkedHashSet<>();
	private Entity parent;
	private List<Leader> leaders = new ArrayList<>();
	@Xml(value = "child", elementClass = Integer.class, multiple = true)
	private List<Integer> children = new ArrayList<>();
	@Xml(value = "entity_link", elementClass = EntityLink.class, multiple = true)
	private List<EntityLink> entityLinks = new ArrayList<>();
	@Xml(value = "entity_position", elementClass = EntityPosition.class, multiple = true)
	private Map<Integer, EntityPosition> positions = new HashMap<>();
	@Xml(value = "entity_position_assignment", elementClass = EntityPositionAssignment.class, multiple = true)
	private Map<Integer, EntityPositionAssignment> assignments = new HashMap<>();
	@Xml(value = "histfig_id", elementClass = Integer.class, multiple = true)
	private List<Integer> hfIds = new ArrayList<>();
	@Xml(value = "worship_id", elementClass = Integer.class, multiple = true)
	private List<Integer> worshipIds = new ArrayList<>();
	@Xml("claims")
	@XmlConverter(CoordListConverter.class)
	private List<Coords> claims = new ArrayList<>();
	@Xml(value = "occasion", elementClass = Occasion.class, multiple = true)
	private Map<Integer, Occasion> occasions = new LinkedHashMap<>();

	private boolean fallen = false;
	
	private static Occasion UNKNOWN_OCCASION = new Occasion();

	public String getName() {
		return EventHelper.name(name);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRace() {
		return race;
	}

	public void setRace(String race) {
		this.race = race;
	}

	public Entity getRoot() {
		if (id == -1)
			return this;
		if (parent == null)
			return this;
		else
			return parent.getRoot();
	}

	public Entity getParent() {
		return parent;
	}

	public void setParent(Entity parent) {
		if (id != -1 && parent.id != -1 && this != parent && parent != null)
			this.parent = parent;
	}

	public Set<Site> getSites() {
		return sites;
	}

	public List<Leader> getLeaders() {
		return leaders;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Integer> getChildren() {
		return children;
	}

	public List<EntityLink> getEntityLinks() {
		return entityLinks;
	}

	public EntityPosition getPosition(int id) {
		return positions.get(id);
	}

	public Map<Integer, EntityPosition> getPositions() {
		return positions;
	}

	public EntityPositionAssignment getAssignment(int id) {
		return assignments.get(id);
	}

	public Map<Integer, EntityPositionAssignment> getAssignments() {
		return assignments;
	}

	public List<Integer> getHfIds() {
		return hfIds;
	}

	public List<Integer> getWorshipIds() {
		return worshipIds;
	}

	public List<Coords> getClaims() {
		return claims;
	}

	public Occasion getOccasion(int id) {
		return occasions.getOrDefault(id, UNKNOWN_OCCASION);
	}
	
	public Collection<Occasion> getOccasions() {
		return occasions.values();
	}

	public boolean isFallen() {
		return fallen;
	}

	public void setFallen(boolean fallen) {
		this.fallen = fallen;
	}

	public static String getColor(String race) {
		switch (race.toLowerCase()) {
		case "kobold":
		case "kobolds":
			return "#333";
		case "goblin":
		case "goblins":
			return "#F0F";
		case "elf":
		case "elves":
			return "#99FF00";
		case "dwarf":
		case "dwarves":
			return "#24E741";
		case "human":
		case "humans":
			return "#0000CC";
		case "necromancer":
		case "necromancers":
			return "#A0A";
		case "jotun":
		case "jotuns":
			return "#FCF3CF";
		case "mandre":
		case "mandres":
			return "#8BDFF8";
		case "orc":
		case "orcs":
			return "#30095E";
		case "gnoll":
		case "gnolls":
			return "#242FE7";
		case "arthra":
		case "arthras":
			return "#CC0000";
		default:
			return "#24E7DB";
		}
	}

	public void process() {
		// entities that only own towers are shown as necromancers
		if (getSites().stream().filter(s -> "tower".equals(s.getType())).collect(Collectors.counting()) > 0)
			setRace("necromancers");

		// mark sites pillaged to have no remaining population as ruin
		if (World.isPopulationavailable())
			getSites().stream().filter(s -> s.getPopulations().isEmpty()).forEach(s -> s.setRuin(true));

		// mark civilizations that own no sites or only ruins as fallen
		if (type.equals("civilization")) {
			long siteCount = getSites().stream()
					.filter(s -> !s.isRuin() && s.getOwner() != null && this.equals(s.getOwner().getRoot()))
					.collect(Collectors.counting());
			if (siteCount == 0)
				setFallen(true);
		}

	}

	public String getColor() {
		if (id == -1)
			return "#ddf";

		return Entity.getColor(race);
	}

	@Override
	public String toString() {
		return "[" + id + "] " + getName();
	}

	public String getURL() {
		return Application.getSubUri() + "/entity/" + id;
	}

	public static String getGlyph(String type) {
		switch (type) {
		case "sitegovernment":
			return "fa fa-balance-scale";
		case "outcast":
			return "glyphicon glyphicon-tent";
		case "nomadicgroup":
			return "glyphicon glyphicon-tree-deciduous";
		case "religion":
			return "fa fa-university";
		case "performancetroupe":
			return "glyphicon glyphicon-cd";
		case "migratinggroup":
			return "glyphicon glyphicon-transfer";

		case "civilization":
		default:
			return "glyphicon glyphicon-star";
		}
	}

	public String getGlyph() {
		if (isFallen())
			return "glyphicon glyphicon-star-empty";
		return getGlyph(type);
	}

	private String getIcon() {
		return "<span class=\"" + getGlyph() + "\" style=\"color: " + getColor() + "\" aria-hidden=\"true\"></span> ";
	}

	public String getLink() {
		if (id == -1)
			return "<i>UNKNOWN ENTITY</i>";

		return "<a href=\"" + getURL() + "\" class=\"entity\">" + getIcon() + getName() + "</a>";
	}

	public List<Entity> getWarEnemies() {
		return World.getHistoricalEventCollections().stream().filter(e -> e instanceof WarCollection)
				.map(e -> (WarCollection) e)
				.filter(e -> e.getAggressorEntId() == getId() || e.getDefenderEntId() == getId()).map(e -> {
					if (e.getAggressorEntId() == getId())
						return e.getDefenderEntId();
					else
						return e.getAggressorEntId();
				}).map(World::getEntity).collect(Collectors.toList());

	}

	public List<WarCollection> getWars() {
		return World.getHistoricalEventCollections().stream().filter(e -> e instanceof WarCollection)
				.map(e -> (WarCollection) e)
				.filter(e -> e.getAggressorEntId() == getId() || e.getDefenderEntId() == getId())
				.collect(Collectors.toList());
	}

	public List<Entity> getGroups() {
		return World.getEntities().stream()
				.filter(e -> !e.equals(this) && (this.equals(e.getParent()) || this.equals(e.getRoot())))
				.collect(Collectors.toList());
	}

	public List<OccasionCollection> getOccasionCollections() {
		return World.getHistoricalEventCollections().stream()
				.collect(Filters.filterCollection(OccasionCollection.class, c -> c.getCivId() == id))
				.collect(Collectors.toList());
	}

}
