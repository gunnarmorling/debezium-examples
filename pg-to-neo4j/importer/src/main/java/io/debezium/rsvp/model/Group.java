package io.debezium.rsvp.model;

import java.util.List;

import javax.json.bind.annotation.JsonbProperty;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="MeetupGroup")
public class Group {

    @Id
    @JsonbProperty("group_id")
    public String id;

    @JsonbProperty("group_name")
    public String name;

    @JsonbProperty("group_city")
    public String city;

    @JsonbProperty("group_country")
    public String country;

    @JsonbProperty("group_lon")
    public double lon;

    @JsonbProperty("group_lat")
    public double lat;

    @JsonbProperty("group_urlname")
    public String urlname;

    @JsonbProperty("group_state")
    public String state;

    @ElementCollection
    @JsonbProperty("group_topics")
    public List<GroupTopic> topics;

    @Override
    public String toString() {
        return "Group [id=" + id + ", name=" + name + ", city=" + city + ", country=" + country + ", lon=" + lon
                + ", lat=" + lat + ", urlname=" + urlname + ", state=" + state + ", topics=" + topics + "]";
    }

    @Embeddable
    public static class GroupTopic {

        @JsonbProperty("urlkey")
        public String urlKey;

        @JsonbProperty("topic_name")
        public String name;

        @Override
        public String toString() {
            return "GroupTopic [urlKey=" + urlKey + ", name=" + name + "]";
        }
    }
}
