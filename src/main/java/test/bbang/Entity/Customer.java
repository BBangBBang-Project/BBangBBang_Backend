package test.bbang.Entity;

import jakarta.persistence.*;


@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId; // 유저 식별자 ID

    @Column(nullable = false, unique = true)
    private String username; // 실제 치는 ID

    @Column(nullable = false)
    private String password; // 비밀번호

    @Column(nullable = false)
    private String name; // 이름

    @Column(nullable = false)
    private String quickPassword; // 간편비밀번호

    // Getters and Setters...

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuickPassword() {
        return quickPassword;
    }

    public void setQuickPassword(String quickPassword) {
        this.quickPassword = quickPassword;
    }
}
