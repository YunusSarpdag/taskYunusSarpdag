package com.example.gerimedica.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileModelRepository extends JpaRepository<FileModel, String> {
    List<FileModel> findByFile(File file);
}
