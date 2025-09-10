package com.crus.customerWebsite.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Date;
import java.util.Objects;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "customers")
@Builder
@Getter
@Setter
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Integer version;

    private String fullName;
    private String emailAddress;
    private Integer age;
    private String address;
    private Date processedData;

    @OneToOne(mappedBy = "customer")
    private Book book;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id) && Objects.equals(version, customer.version) && Objects.equals(fullName, customer.fullName) && Objects.equals(emailAddress, customer.emailAddress) && Objects.equals(age, customer.age) && Objects.equals(address, customer.address) && Objects.equals(processedData, customer.processedData) && Objects.equals(book, customer.book);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, version, fullName, emailAddress, age, address, processedData, book);
    }
}
