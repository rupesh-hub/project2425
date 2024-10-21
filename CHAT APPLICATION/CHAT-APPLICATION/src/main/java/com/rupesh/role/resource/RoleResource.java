package com.rupesh.role.resource;

import com.rupesh.role.service.IRoleService;
import com.rupesh.shared.GlobalResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleResource {

    private final IRoleService roleService;

    @PostMapping("/save")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<GlobalResponse<Void>> save(@RequestBody @Valid Set<String> request) {
        return ResponseEntity.ok(roleService.save(request));
    }

    @GetMapping("/get.all")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<GlobalResponse<Set<String>>> getAll(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(roleService.getAll(page, size));
    }

    @GetMapping("/by.name/{role}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<GlobalResponse<Set<String>>> getByName(@PathVariable String role) {
        return ResponseEntity.ok(roleService.getByName(role));
    }

}