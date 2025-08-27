package com.crus.customerWebsite.repos;

import com.crus.customerWebsite.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByRole(Role.Roles roles);
}
