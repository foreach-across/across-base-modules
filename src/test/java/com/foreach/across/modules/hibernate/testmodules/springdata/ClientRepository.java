package com.foreach.across.modules.hibernate.testmodules.springdata;

import org.springframework.data.repository.CrudRepository;

public interface ClientRepository extends CrudRepository<Client, Long>
{
}
