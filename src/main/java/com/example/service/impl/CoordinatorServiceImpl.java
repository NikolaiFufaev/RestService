package com.example.service.impl;

import com.example.entity.Coordinator;
import com.example.exception.NotFoundException;
import com.example.repository.CoordinatorRepository;
import com.example.repository.impl.CoordinatorRepositoryImpl;
import com.example.service.CoordinatorService;
import com.example.servlet.dto.CoordinatorDto;
import com.example.servlet.mapper.CoordinatorMapper;
import com.example.servlet.mapper.impl.CoordinatorMapperImpl;

import java.util.List;

public class CoordinatorServiceImpl implements CoordinatorService {
    private final CoordinatorMapper coordinatorMapper = CoordinatorMapperImpl.getInstance();
    private static CoordinatorService instance;
    private final CoordinatorRepository coordinatorRepository = CoordinatorRepositoryImpl.getInstance();


    private CoordinatorServiceImpl() {
    }

    public static synchronized CoordinatorService getInstance() {
        if (instance == null) {
            instance = new CoordinatorServiceImpl();
        }
        return instance;
    }

    @Override
    public CoordinatorDto save(CoordinatorDto coordinatorDto) {
        Coordinator coordinator = coordinatorRepository.save(coordinatorMapper.mapFromDto(coordinatorDto));
        return coordinatorMapper.mapToDto(coordinatorRepository.findById(coordinator.getId()).orElse(coordinator));
    }

    @Override
    public void update(CoordinatorDto coordinatorDto) throws NotFoundException {
        if (coordinatorDto == null || coordinatorDto.getId() == null) {
            throw new IllegalArgumentException();
        }
        coordinatorRepository.update(coordinatorMapper.mapFromDto(coordinatorDto));
    }

    @Override
    public CoordinatorDto findById(Long coordinatorId) throws NotFoundException {
        Coordinator coordinator = coordinatorRepository.findById(coordinatorId).orElseThrow();
        return coordinatorMapper.mapToDto(coordinator);
    }

    @Override
    public List<CoordinatorDto> findAll() {
        List<Coordinator> coordinatorList = coordinatorRepository.findAll();
        return coordinatorMapper.mapToListDto(coordinatorList);
    }

    @Override
    public void delete(Long coordinatorId) throws NotFoundException {
        coordinatorRepository.deleteById(coordinatorId);
    }
}
