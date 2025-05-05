package com.look.entity;


import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "roles") // Nueva colección "roles"
public class Role {

    @Id
    private String id; // ObjectId de MongoDB

    @Indexed(unique = true) // El nombre del rol debe ser único
    @Field("name")
    private String name; // Ej: "ROLE_USER", "ROLE_ADMIN", "ROLE_SUPERADMIN"

    // Podrías añadir más campos en el futuro, como permisos:
    // private Set<String> permissions;
}