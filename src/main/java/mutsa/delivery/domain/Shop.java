package mutsa.delivery.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "shops")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String contact;
    private String location;

    @Builder(access = AccessLevel.PRIVATE)
    private Shop(String name, String contact, String location) {
        this.name = name;
        this.contact = contact;
        this.location = location;
    }

    public static Shop create(String name, String contact, String location) {
        return Shop.builder()
                .name(name)
                .contact(contact)
                .location(location)
                .build();
    }
}
