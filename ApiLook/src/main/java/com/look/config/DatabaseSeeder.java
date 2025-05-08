package com.look.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn; // Para asegurar el orden si es necesario
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.look.entity.Chat;
import com.look.entity.Comment;
import com.look.entity.Like;
import com.look.entity.Message;
import com.look.entity.Post;
import com.look.entity.Role;
import com.look.entity.User;
import com.look.repository.ChatRepository;
import com.look.repository.CommentRepository;
import com.look.repository.LikeRepository;
import com.look.repository.PostRepository;
import com.look.repository.RoleRepository;
import com.look.repository.UserRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
public class DatabaseSeeder {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSeeder.class);

    // --- Repositorios ---
    @Autowired private UserRepository  userRepository;
    @Autowired private PostRepository  postRepository;
    @Autowired private CommentRepository  commentRepository;
    @Autowired private LikeRepository  likeRepository;
    @Autowired private ChatRepository  chatRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private RoleRepository roleRepository;

    // --- Datos de Ejemplo ---
    // SuperAdmin 
    private static final String DEFAULT_SUPERADMIN_USERNAME = "superadmin";
    private static final String DEFAULT_SUPERADMIN_EMAIL = "superadmin@example.com";
    private static final String DEFAULT_SUPERADMIN_PASSWORD = "superadminpassword"; 

    // Usuarios de ejemplo
    private static final String USER1_USERNAME = "alice";
    private static final String USER1_EMAIL = "alice@example.com";
    private static final String USER1_PASSWORD = "password123";

    private static final String USER2_USERNAME = "bob";
    private static final String USER2_EMAIL = "bob@example.com";
    private static final String USER2_PASSWORD = "password456";

    // Posts de ejemplo
    private static final String POST1_TITLE = "Mi primer viaje";
    private static final String POST2_TITLE = "Receta de cocina";


    @Bean
    CommandLineRunner seedDatabaseRunner() {
        return args -> {
            log.info("Starting database seeding...");
            
            Role roleUser = createRoleIfNeeded("ROLE_USER");
            Role roleAdmin = createRoleIfNeeded("ROLE_ADMIN");
            Role roleSuperAdmin = createRoleIfNeeded("ROLE_SUPERADMIN");

         // 1. Crear Usuarios (referenciando los objetos Role)
            User superAdmin = createSuperAdminIfNeeded(Set.of(roleUser, roleAdmin, roleSuperAdmin)); // Pasar Set<Role>
            User userAlice = createUserIfNeeded(USER1_USERNAME, USER1_EMAIL, USER1_PASSWORD, Set.of(roleUser)); // Pasar Set<Role>
            User userBob = createUserIfNeeded(USER2_USERNAME, USER2_EMAIL, USER2_PASSWORD, Set.of(roleUser)); // Pasar Set<Role>

            // Verificar que los usuarios existen para continuar
            if (userAlice == null || userBob == null) {
                 log.warn("Sample users could not be created or found. Skipping dependent data seeding.");
                 return; // Salir si los usuarios base no están
            }


            // 2. Crear Posts
            Post  post1 = createPostIfNeeded(POST1_TITLE, "Fotos y descripción de mi viaje a las montañas.", "http://example.com/mountains.jpg", userAlice);
            Post post2 = createPostIfNeeded(POST2_TITLE, "Cómo hacer la mejor lasaña.", null, userBob);


             // Verificar que los posts existen para continuar
            if (post1 == null || post2 == null) {
                 log.warn("Sample posts could not be created or found. Skipping dependent data seeding.");
                 return;
            }


            // 3. Crear Comentarios
            createCommentIfNeeded("¡Qué vistas tan increíbles!", post1, userBob);
            createCommentIfNeeded("Me encanta esa receta, ¡la probaré!", post2, userAlice);
            createCommentIfNeeded("Yo también estuve allí, es hermoso.", post1, superAdmin); // SuperAdmin comenta


            // 4. Crear Likes
            createLikeIfNeeded(post1, userBob); 
            createLikeIfNeeded(post2, userAlice); 
            createLikeIfNeeded(post1, userAlice);
            createLikeIfNeeded(post1, superAdmin); 


            // 5. Crear Chats y Mensajes
            createChatIfNeeded(userAlice, userBob);
            createChatIfNeeded(userAlice, superAdmin); 


            log.info("Database seeding finished.");
        };
    }

    // --- Métodos Helper para crear si no existen ---
    
    // --- NUEVO: Método Helper para crear Roles ---
    private Role createRoleIfNeeded(String roleName) {
        return roleRepository.findByName(roleName).orElseGet(() -> {
            log.info("Creating role: {}", roleName);
            Role newRole = Role.builder().name(roleName).build();
            return roleRepository.save(newRole);
        });
    }

    private User createSuperAdminIfNeeded(Set<Role> roles) { // Acepta Set<Role>
        return userRepository.findByUsername(DEFAULT_SUPERADMIN_USERNAME).orElseGet(() -> {
            log.info("Creating default superadmin user...");
            User superAdmin = User.builder()
                    .username(DEFAULT_SUPERADMIN_USERNAME)
                    .email(DEFAULT_SUPERADMIN_EMAIL)
                    .password(passwordEncoder.encode(DEFAULT_SUPERADMIN_PASSWORD))
                    .createdAt(new Date())
                    .enabled(true)
                    .accountNonExpired(true)
                    .accountNonLocked(true)
                    .credentialsNonExpired(true)
                    .roles(new HashSet<>(roles)) // Asignar el Set<Role>
                    .build();
            return userRepository.save(superAdmin);
        });
    }

    private User createUserIfNeeded(String username, String email, String password, Set<Role> roles) { // Acepta Set<Role>
        return userRepository.findByUsername(username).orElseGet(() -> {
            log.info("Creating sample user: {}", username);
            User user = User.builder()
                    .username(username)
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .createdAt(new Date())
                    .enabled(true)
                    .accountNonExpired(true)
                    .accountNonLocked(true)
                    .credentialsNonExpired(true)
                    .roles(new HashSet<>(roles)) // Asignar el Set<Role>
                    .build();
            return userRepository.save(user);
        });
   }

     private Post createPostIfNeeded(String title, String content, String imageUri, User author) {
         // Comprobar por título y autor para evitar duplicados simples
         List<Post> existing = postRepository.findByUserIdOrderByCreatedAtDesc(author.getId()); // Optimizable si hay muchos posts
         if (existing.stream().anyMatch(p -> title.equals(p.getTitle()))) {
             // Encontrar y devolver el post existente si se necesita el ID exacto
             return existing.stream().filter(p -> title.equals(p.getTitle())).findFirst().orElse(null);
         }

         log.info("Creating sample post titled: {}", title);
         Post post = Post.builder()
                 .title(title)
                 .content(content)
                 .imageUri(imageUri)
                 .userId(author.getId())
                 .createdAt(new Date())
                 .build();
         return postRepository.save(post);
     }


     private void createCommentIfNeeded(String content, Post post, User author) {
         // Comprobar si ya existe un comentario *exacto* de este autor en este post
         List<Comment> existing = commentRepository.findByPostIdOrderByCreatedAtAsc(post.getId());
         boolean alreadyExists = existing.stream()
                 .anyMatch(c -> author.getId().equals(c.getUserId()) && content.equals(c.getContent()));

         if (!alreadyExists) {
              log.info("Creating sample comment by {} on post '{}'", author.getUsername(), post.getTitle());
             Comment comment = Comment.builder()
                     .content(content)
                     .postId(post.getId())
                     .userId(author.getId())
                     .createdAt(new Date())
                     .build();
             commentRepository.save(comment);
         }
     }


     private void createLikeIfNeeded(Post post, User user) {
         boolean alreadyExists = likeRepository.findByPostIdAndUserId(post.getId(), user.getId()).isPresent();
         if (!alreadyExists) {
             log.info("Creating sample like by {} on post '{}'", user.getUsername(), post.getTitle());
             Like  like = Like.builder()
                     .postId(post.getId())
                     .userId(user.getId())
                     .createdAt(new Date())
                     .build();
             likeRepository.save(like);
         }
     }


    private void createChatIfNeeded(User user1, User user2) {
        boolean alreadyExists = chatRepository.findByUser1IdAndUser2IdOrUser2IdAndUser1Id(
                user1.getId(), user2.getId(), user1.getId(), user2.getId()
        ).isPresent();

        if (!alreadyExists) {
            log.info("Creating sample chat between {} and {}", user1.getUsername(), user2.getUsername());

            // Crear algunos mensajes de ejemplo dentro del chat
            List<Message > messages = new ArrayList<>();
            messages.add(Message.builder()
                    .senderId(user1.getId())
                    .message("Hola " + user2.getUsername() + "!")
                    .timestamp(new Date(System.currentTimeMillis() - 50000)) // Mensaje un poco más antiguo
                    .build());
             messages.add(Message.builder()
                    .senderId(user2.getId())
                    .message("¡Hola " + user1.getUsername() + "! ¿Cómo estás?")
                    .timestamp(new Date(System.currentTimeMillis() - 20000)) // Mensaje más reciente
                    .build());


            Chat  chat = Chat.builder()
                    .user1Id(user1.getId())
                    .user2Id(user2.getId())
                    .messages(messages) // Añadir la lista de mensajes
                    .build();
            chatRepository.save(chat);
        }
    }
}