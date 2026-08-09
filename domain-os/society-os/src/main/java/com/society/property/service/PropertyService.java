package com.society.property.service;

import com.society.property.entity.PropertyEntity;
import com.society.property.entity.OwnershipHistoryEntity;
import java.util.List;
import java.util.Optional;

public interface PropertyService {
    PropertyEntity saveProperty(PropertyEntity property);
    Optional<PropertyEntity> getPropertyById(Integer id);
    Optional<PropertyEntity> getPropertyByNumber(String propertyNumber);
    List<PropertyEntity> getAllProperties();
    List<PropertyEntity> getPropertiesByOwner(Integer memberId);
    
    OwnershipHistoryEntity transferOwnership(Integer propertyId, Integer newMemberId, String transferDate);
    List<OwnershipHistoryEntity> getOwnershipHistory(Integer propertyId);
}
