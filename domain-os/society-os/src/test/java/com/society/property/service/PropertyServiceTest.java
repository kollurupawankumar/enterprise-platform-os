package com.society.property.service;

import com.society.member.entity.MemberEntity;
import com.society.member.repository.MemberRepository;
import com.society.property.entity.OwnershipHistoryEntity;
import com.society.property.entity.PropertyEntity;
import com.society.property.repository.OwnershipHistoryRepository;
import com.society.property.repository.PropertyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PropertyServiceTest {

    @Mock
    private PropertyRepository propertyRepository;
    @Mock
    private OwnershipHistoryRepository ownershipHistoryRepository;
    @Mock
    private MemberRepository memberRepository;

    private PropertyService propertyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        propertyService = new PropertyServiceImpl(propertyRepository, ownershipHistoryRepository, memberRepository);
    }

    @Test
    void testSaveProperty() {
        PropertyEntity p = new PropertyEntity();
        p.setPropertyNumber("101");
        p.setBlock("A");
        p.setType("FLAT");

        when(propertyRepository.save(any(PropertyEntity.class))).thenReturn(p);

        PropertyEntity saved = propertyService.saveProperty(p);
        assertNotNull(saved);
        assertEquals("101", saved.getPropertyNumber());
    }

    @Test
    void testTransferOwnership() {
        PropertyEntity p = new PropertyEntity();
        p.setId(1);
        p.setPropertyNumber("101");

        MemberEntity m = new MemberEntity();
        m.setId(2);

        when(propertyRepository.findById(1)).thenReturn(Optional.of(p));
        when(memberRepository.findById(2)).thenReturn(Optional.of(m));
        
        List<OwnershipHistoryEntity> mockHistory = new ArrayList<>();
        OwnershipHistoryEntity oldHistory = new OwnershipHistoryEntity();
        oldHistory.setProperty(p);
        oldHistory.setFromDate("2020-01-01");
        oldHistory.setToDate(null);
        mockHistory.add(oldHistory);

        when(ownershipHistoryRepository.findByPropertyId(1)).thenReturn(mockHistory);
        when(ownershipHistoryRepository.save(any(OwnershipHistoryEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        OwnershipHistoryEntity result = propertyService.transferOwnership(1, 2, "2026-08-04");

        assertNotNull(result);
        assertEquals("2026-08-04", result.getFromDate());
        assertEquals(m, p.getCurrentOwner());
        assertEquals("2026-08-04", oldHistory.getToDate());
    }
}
