package com.microfinance.customer_microservice.application.service;
import java.util.List;
import java.util.UUID;

import com.microfinance.customer_microservice.application.dto.input.ContactInfoCreateDTO;
import com.microfinance.customer_microservice.application.dto.output.ContactInfoResponseDTO;
import com.microfinance.customer_microservice.application.dto.input.ContactInfoUpdateDTO;
import com.microfinance.customer_microservice.application.dto.input.ContactInfoReplaceDTO;

public interface IContactInfoService {
    ContactInfoResponseDTO createContactInfo(ContactInfoCreateDTO contactInfoCreateDTO);
    ContactInfoResponseDTO getContactInfoById(UUID id);
    List<ContactInfoResponseDTO> getAllContactInfo();
    void deleteContactInfo(UUID id);
    ContactInfoResponseDTO updateContactInfo(UUID id, ContactInfoUpdateDTO contactInfoUpdateDTO);
    ContactInfoResponseDTO replaceContactInfo(UUID id, ContactInfoReplaceDTO contactInfoReplaceDTO);
}
