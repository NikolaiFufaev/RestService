package com.example.servlet.mapper.impl;

import com.example.entity.Coordinator;
import com.example.servlet.dto.CoordinatorDto;
import com.example.servlet.mapper.CoordinatorMapper;
import com.example.servlet.mapper.StudentMapper;

import java.util.ArrayList;
import java.util.List;

public class CoordinatorMapperImpl implements CoordinatorMapper {
    private static final StudentMapper studentMapper = StudentMapperImpl.getInstance();
    private static CoordinatorMapper instance;

    private CoordinatorMapperImpl() {

    }

    public static synchronized CoordinatorMapper getInstance() {
        if (instance == null) {
            instance = new CoordinatorMapperImpl();
        }
        return instance;
    }


    @Override
    public Coordinator mapFromDto(CoordinatorDto dto) {
        Coordinator coordinator = new Coordinator();
        if (dto.getId() != null){
            coordinator.setId(dto.getId());
        }
            coordinator.setName(dto.getName());
        if (dto.getStudentDtoList() != null && !dto.getStudentDtoList().isEmpty()) {
            coordinator.setStudents(studentMapper.mapFromListDto(dto.getStudentDtoList()));
        }
        return coordinator;
    }

    @Override
    public CoordinatorDto mapToDto(Coordinator entity) {
        CoordinatorDto dto = new CoordinatorDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        if (entity.getStudents() != null) {
            dto.setStudentDtoList(
                    studentMapper.mapToListDto(entity.getStudents()));
        }

        return dto;
    }

    @Override
    public List<CoordinatorDto> mapToListDto(List<Coordinator> listEntity) {
        List<CoordinatorDto> dtoList = new ArrayList<>();
        for (Coordinator coordinator : listEntity) {
            dtoList.add(mapToDto(coordinator));
        }
        return dtoList;
    }

}
