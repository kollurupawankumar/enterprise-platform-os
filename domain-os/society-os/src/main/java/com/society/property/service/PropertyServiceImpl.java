package com.society.property.service;

import com.society.member.entity.MemberEntity;
import com.society.member.repository.MemberRepository;
import com.society.property.entity.OwnershipHistoryEntity;
import com.society.property.entity.PropertyEntity;
import com.society.property.repository.OwnershipHistoryRepository;
import com.society.property.repository.PropertyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;
    private final OwnershipHistoryRepository ownershipHistoryRepository;
    private final MemberRepository memberRepository;

    public PropertyServiceImpl(
            PropertyRepository propertyRepository,
            OwnershipHistoryRepository ownershipHistoryRepository,
            MemberRepository memberRepository) {
        this.propertyRepository = propertyRepository;
        this.ownershipHistoryRepository = ownershipHistoryRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    public PropertyEntity saveProperty(PropertyEntity property) {
        if (property != null && property.getPropertyNumber() != null && property.getBlock() != null && property.getType() != null) {
            boolean exists = propertyRepository.existsByPropertyNumberIgnoreCaseAndBlockIgnoreCaseAndTypeIgnoreCase(
                    property.getPropertyNumber().trim(),
                    property.getBlock().trim(),
                    property.getType().trim()
            );
            if (exists) {
                throw new IllegalArgumentException("Property unit '" + property.getPropertyNumber() + "' (Block: " + property.getBlock() + ", Type: " + property.getType() + ") already exists in the system.");
            }
        }
        return propertyRepository.save(property);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PropertyEntity> getPropertyById(Integer id) {
        return propertyRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PropertyEntity> getPropertyByNumber(String propertyNumber) {
        return propertyRepository.findByPropertyNumber(propertyNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropertyEntity> getAllProperties() {
        return propertyRepository.findAllWithOwners();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropertyEntity> getPropertiesByOwner(Integer memberId) {
        return propertyRepository.findByCurrentOwnerId(memberId);
    }

    @Override
    public OwnershipHistoryEntity transferOwnership(Integer propertyId, Integer newMemberId, String transferDate) {
        PropertyEntity property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new IllegalArgumentException("Property not found with ID: " + propertyId));

        MemberEntity newOwner = memberRepository.findById(newMemberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with ID: " + newMemberId));

        // Find active ownership history and close it
        List<OwnershipHistoryEntity> historyList = ownershipHistoryRepository.findByPropertyId(propertyId);
        for (OwnershipHistoryEntity history : historyList) {
            if (history.getToDate() == null) {
                history.setToDate(transferDate);
                ownershipHistoryRepository.save(history);
            }
        }

        // Set current owner
        property.setCurrentOwner(newOwner);
        propertyRepository.save(property);

        // Record new history entry
        OwnershipHistoryEntity newHistory = new OwnershipHistoryEntity();
        newHistory.setProperty(property);
        newHistory.setMember(newOwner);
        newHistory.setFromDate(transferDate);
        newHistory.setToDate(null);

        return ownershipHistoryRepository.save(newHistory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OwnershipHistoryEntity> getOwnershipHistory(Integer propertyId) {
        return ownershipHistoryRepository.findByPropertyId(propertyId);
    }
}
