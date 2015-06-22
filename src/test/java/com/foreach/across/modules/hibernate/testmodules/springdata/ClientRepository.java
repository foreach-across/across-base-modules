package com.foreach.across.modules.hibernate.testmodules.springdata;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long>
{
}
