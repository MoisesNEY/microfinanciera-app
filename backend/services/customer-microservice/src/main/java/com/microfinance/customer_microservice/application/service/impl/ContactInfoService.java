package com.microfinance.customer_microservice.application.service.impl;
import com.microfinance.customer_microservice.application.dto.input.ContactInfoCreateDTO;
import com.microfinance.customer_microservice.application.dto.input.ContactInfoReplaceDTO;
import com.microfinance.customer_microservice.application.dto.input.ContactInfoUpdateDTO;
import com.microfinance.customer_microservice.application.dto.output.ContactInfoResponseDTO;
import com.microfinance.customer_microservice.application.service.IContactInfoService;
import com.microfinance.customer_microservice.domain.entity.ContactInfoEntity;
import com.microfinance.customer_microservice.application.dto.mapper.ContactInfoMapper;
import com.microfinance.customer_microservice.infrastructure.repository.ContactInfoRepository;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class ContactInfoService implements IContactInfoService {

    private final ContactInfoRepository contactInfoRepository;
    private final ContactInfoMapper contactInfoMapper;

    public ContactInfoService(ContactInfoRepository contactInfoRepository, ContactInfoMapper contactInfoMapper) {
        this.contactInfoRepository = contactInfoRepository;
        this.contactInfoMapper = contactInfoMapper;
    }

    public ContactInfoResponseDTO createContactInfo(ContactInfoCreateDTO contactInfoCreateDTO) {
        ContactInfoEntity contactInfo =  contactInfoMapper.toContactInfoEntity(contactInfoCreateDTO);
        contactInfo = contactInfoRepository.save(contactInfo);
        return contactInfoMapper.toContactInfoResponseDTO(contactInfo);
    }

    public ContactInfoResponseDTO getContactInfoById(UUID id) {
        ContactInfoEntity contactInfo = contactInfoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Contact Info not found"));
        return contactInfoMapper.toContactInfoResponseDTO(contactInfo);
    }

    public List<ContactInfoResponseDTO> getAllContactInfo() {
        return contactInfoRepository.findAll().stream()
            .map(contactInfoMapper::toContactInfoResponseDTO)
            .toList();
    }

    public void deleteContactInfo(UUID id) {
        contactInfoRepository.deleteById(id);
    }

    public ContactInfoResponseDTO updateContactInfo(UUID id, ContactInfoUpdateDTO contactInfoUpdateDTO) {
        ContactInfoEntity contactInfo = contactInfoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Contact Info not found"));
        if (contactInfoUpdateDTO.getContactType() != null) {
            contactInfo.setContactType(contactInfoUpdateDTO.getContactType());
        }
        if (contactInfoUpdateDTO.getContactValue() != null) {
            contactInfo.setContactValue(contactInfoUpdateDTO.getContactValue());
        }
        if (contactInfoUpdateDTO.getIsPrimary() != null) {
            contactInfo.setPrimary(contactInfoUpdateDTO.getIsPrimary());
        }
        contactInfo = contactInfoRepository.save(contactInfo);
        return contactInfoMapper.toContactInfoResponseDTO(contactInfo);
    }

    public ContactInfoResponseDTO replaceContactInfo(UUID id, ContactInfoReplaceDTO contactInfoReplaceDTO) {
        ContactInfoEntity contactInfo = contactInfoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Contact Info not found"));
        contactInfo.setContactType(contactInfoReplaceDTO.getContactType());
        contactInfo.setContactValue(contactInfoReplaceDTO.getContactValue());
        contactInfo.setPrimary(contactInfoReplaceDTO.isPrimary());

        contactInfo = contactInfoRepository.save(contactInfo);
        return contactInfoMapper.toContactInfoResponseDTO(contactInfo);
    }
}
