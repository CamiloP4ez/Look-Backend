package com.look.entity;


import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;
import java.util.HashSet;
import java.util.Date;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "usuarios")
public class User {

    @Id
    private String id; 

    @Indexed(unique = true)
    @Field("username")
    private String username;

    @Indexed(unique = true) 
    @Field("email")
    private String email;

    @Field("password_hash") 
    private String password; 

    @Field("profile_picture_uri")
    private String profilePictureUri;

    @Field("created_at")
    private Date createdAt;

    
    @Field("enabled")
    private boolean enabled;

    @Field("account_non_expired")
    private boolean accountNonExpired;

    @Field("account_non_locked")
    private boolean accountNonLocked;

    @Field("credentials_non_expired")
    private boolean credentialsNonExpired;

    @DBRef(lazy = true) // Carga perezosa para mejor rendimiento
    @Builder.Default // Para inicializar con @Builder
    @Field("roles")
    private Set<Role> roles = new HashSet<>(); // Ahora es Set<Role>, no Set<String>
}