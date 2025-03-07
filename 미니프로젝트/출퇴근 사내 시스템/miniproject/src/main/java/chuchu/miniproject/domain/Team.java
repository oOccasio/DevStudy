package chuchu.miniproject.domain;

import chuchu.miniproject.domain.worker.Worker;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 25, unique = true)
    private String name;

    @Column(length = 25)
    private String manager;

    @Column
    private Integer memberCount;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Worker> workers = new ArrayList<>();



    public Team(Builder builder) {
        this.name = builder.name;
        this.manager = builder.manager;
        this.memberCount = builder.memberCount;
        this.workers.addAll(builder.workers);
    }

    public static Builder builder(){
        return new Builder();
    }


    public static class Builder{
        private String name;
        private String manager;
        private Integer memberCount;
        private List<Worker> workers = new ArrayList<>();

        public Builder name(String name){
            this.name = name;
            return this;
        }

        public Builder manager(String manager){
            this.manager = manager;
            return this;
        }

        public Builder memberCount(List<Worker> workers){

            this.memberCount = (workers.isEmpty()) ? 0 : workers.size();
            return this;
        }

        public Builder workers(List<Worker> workers){
            this.workers = new ArrayList<>(workers);
            return this;
        }

        public Team build(){
            return new Team(this);
        }
    }

    public void updateMemberCount(){
        this.memberCount = workers.size();
    }

    public void addWorker(Worker worker){
        this.workers.add(worker);
        updateMemberCount();
    }

    public void updateManager(String manager)
    {
        this.manager = manager;
    }
}
