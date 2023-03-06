package com.jpenacho.starwarsmoviesrepo.datasource.repository.character;

import com.jpenacho.starwarsmoviesrepo.exception.ResourceNotFound;
import com.jpenacho.starwarsmoviesrepo.datasource.repository.ExternalIdDatabaseService;
import com.jpenacho.starwarsmoviesrepo.datasource.repository.TransactionService;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Log4j2
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
public class CharacterDatabaseServiceImpl
        extends TransactionService
        implements ExternalIdDatabaseService<CharacterEntity, UUID, Integer> {

    CharacterRepository characterRepository;

    protected CharacterDatabaseServiceImpl(PlatformTransactionManager transactionManager, CharacterRepository characterRepository) {
        super(transactionManager);
        this.characterRepository = characterRepository;
    }

    @Override
    public Mono<CharacterEntity> read(UUID id) {
        return readOnlyTx(() -> execRead(id));
    }

    @SneakyThrows
    private CharacterEntity execRead(UUID id) {
        return characterRepository.findById(id)
                .map(CharacterEntityMappers.INSTANCE::map)
                .orElseThrow(ResourceNotFound::new);
    }

    @Override
    public Mono<CharacterEntity> readByExternalId(Integer externalId) {
        return readOnlyTx(() -> execReadByExternalId(externalId));
    }

    @SneakyThrows
    private CharacterEntity execReadByExternalId(Integer externalId) {
        CharacterEntity characterEntity = characterRepository.findByExternalId(externalId);

        if (characterEntity == null) {
            throw new ResourceNotFound();
        }

        return CharacterEntityMappers.INSTANCE.map(characterEntity);
    }

    @Override
    public Flux<CharacterEntity> list() {
        return readOnlyTx(this::execList)
                .flatMapIterable(list -> list);
    }

    private Set<CharacterEntity> execList() {
        Iterable<CharacterEntity> characterEntities = characterRepository.findAll();

        return StreamSupport.stream(characterEntities.spliterator(), false)
                .map(CharacterEntityMappers.INSTANCE::map)
                .collect(Collectors.toSet());
    }

    @Override
    public Mono<CharacterEntity> create(CharacterEntity newEntity) {
        return startTx(() -> execSave(newEntity));
    }

    private CharacterEntity execSave(CharacterEntity newEntity) {
        CharacterEntity savedCharacterEntity = characterRepository.save(newEntity);
        return CharacterEntityMappers.INSTANCE.map(savedCharacterEntity);
    }

    @Override
    public Mono<CharacterEntity> update(UUID id, CharacterEntity updatedCharacterEntity) {
        return startTx(() -> execUpdate(id, updatedCharacterEntity));

    }

    @SneakyThrows
    private CharacterEntity execUpdate(UUID id, CharacterEntity updatedCharacterEntity) {
        CharacterEntity characterEntity = characterRepository.findById(id)
                .orElseThrow(ResourceNotFound::new);

        CharacterEntity patchedCharacterEntity = CharacterEntityMappers.INSTANCE.updateEntity(characterEntity, updatedCharacterEntity);

        patchedCharacterEntity = execSave(patchedCharacterEntity);

        return CharacterEntityMappers.INSTANCE.map(patchedCharacterEntity);
    }

    @Override
    public Mono<UUID> delete(UUID id) {
        return startTx(() -> execDelete(id));
    }

    @SneakyThrows
    private UUID execDelete(UUID id) {
        CharacterEntity characterEntity = characterRepository.findById(id)
                .orElseThrow(ResourceNotFound::new);

        characterRepository.delete(characterEntity);

        return id;
    }
}
