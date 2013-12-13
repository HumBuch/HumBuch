package de.dhbw.humbuch.model.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="profile")
public class Profile implements de.dhbw.humbuch.model.entity.Entity {

        @Id
        private int id;
        
        @Enumerated(EnumType.STRING)
        private ProfileType profileType;
        
//        private boolean standard;
//        private boolean french2;
//        private boolean french3;
//        private boolean latin;
//        private boolean science;
//        private boolean evangelic;
//        private boolean romancatholic;
//        private boolean ethics;
        
        public Profile() {}

        public int getId() {
                return id;
        }

        public void setId(int id) {
                this.id = id;
        }

        public ProfileType getProfileType() {
                return profileType;
        }

        public void setProfileType(ProfileType profileType) {
                this.profileType = profileType;
        }
        
}