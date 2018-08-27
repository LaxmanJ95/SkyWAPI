package com.skyrate.dao;

import org.springframework.data.repository.CrudRepository;

import com.skyrate.model.dbentity.Messenger;

public interface MessengerRepository extends CrudRepository<Messenger, Long>{

}
