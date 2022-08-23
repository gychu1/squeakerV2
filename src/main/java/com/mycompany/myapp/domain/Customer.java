package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Customer.
 */
@Entity
@Table(name = "customer")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "slug")
    private String slug;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(mappedBy = "customer")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "customer" }, allowSetters = true)
    private Set<Tweets> tweets = new HashSet<>();

    @ManyToOne
    private User user;

    @ManyToMany
    @JoinTable(
        name = "rel_customer__follower",
        joinColumns = @JoinColumn(name = "customer_id"),
        inverseJoinColumns = @JoinColumn(name = "follower_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "tweets", "user", "followers", "followeds" }, allowSetters = true)
    private Set<Customer> followers = new HashSet<>();

    @ManyToMany(mappedBy = "followers")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "tweets", "user", "followers", "followeds" }, allowSetters = true)
    private Set<Customer> followeds = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Customer id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSlug() {
        return this.slug;
    }

    public Customer slug(String slug) {
        this.setSlug(slug);
        return this;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Customer createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public Customer updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<Tweets> getTweets() {
        return this.tweets;
    }

    public void setTweets(Set<Tweets> tweets) {
        if (this.tweets != null) {
            this.tweets.forEach(i -> i.setCustomer(null));
        }
        if (tweets != null) {
            tweets.forEach(i -> i.setCustomer(this));
        }
        this.tweets = tweets;
    }

    public Customer tweets(Set<Tweets> tweets) {
        this.setTweets(tweets);
        return this;
    }

    public Customer addTweets(Tweets tweets) {
        this.tweets.add(tweets);
        tweets.setCustomer(this);
        return this;
    }

    public Customer removeTweets(Tweets tweets) {
        this.tweets.remove(tweets);
        tweets.setCustomer(null);
        return this;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Customer user(User user) {
        this.setUser(user);
        return this;
    }

    public Set<Customer> getFollowers() {
        return this.followers;
    }

    public void setFollowers(Set<Customer> customers) {
        this.followers = customers;
    }

    public Customer followers(Set<Customer> customers) {
        this.setFollowers(customers);
        return this;
    }

    public Customer addFollower(Customer customer) {
        this.followers.add(customer);
        customer.getFolloweds().add(this);
        return this;
    }

    public Customer removeFollower(Customer customer) {
        this.followers.remove(customer);
        customer.getFolloweds().remove(this);
        return this;
    }

    public Set<Customer> getFolloweds() {
        return this.followeds;
    }

    public void setFolloweds(Set<Customer> customers) {
        if (this.followeds != null) {
            this.followeds.forEach(i -> i.removeFollower(this));
        }
        if (customers != null) {
            customers.forEach(i -> i.addFollower(this));
        }
        this.followeds = customers;
    }

    public Customer followeds(Set<Customer> customers) {
        this.setFolloweds(customers);
        return this;
    }

    public Customer addFollowed(Customer customer) {
        this.followeds.add(customer);
        customer.getFollowers().add(this);
        return this;
    }

    public Customer removeFollowed(Customer customer) {
        this.followeds.remove(customer);
        customer.getFollowers().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Customer)) {
            return false;
        }
        return id != null && id.equals(((Customer) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Customer{" +
            "id=" + getId() +
            ", slug='" + getSlug() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
