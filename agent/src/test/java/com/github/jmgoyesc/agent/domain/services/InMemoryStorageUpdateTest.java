package com.github.jmgoyesc.agent.domain.services;

import com.github.jmgoyesc.agent.domain.models.Configuration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.UUID;

import static com.github.jmgoyesc.agent.domain.models.Configuration.Status.WAITING_FOR_TERMINATION;
import static com.github.jmgoyesc.agent.domain.models.Configuration.TargetDB.mongo;
import static com.github.jmgoyesc.agent.domain.models.Configuration.TargetDB.questdb_ilp;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

/**
 * @author Juan Manuel Goyes Coral
 */

@DisplayName("UT. InMemoryStorageTest - update")
@ExtendWith(MockitoExtension.class)
class InMemoryStorageUpdateTest {

    @InjectMocks private InMemoryStorage target;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @DisplayName("T1. GIVEN a existent configuration, WHEN update, THEN change values in storage")
    @Test
    void test1() {
        //given
        var connectionProperties = new Configuration.ConnectionProperties("url");
        var now = ZonedDateTime.of(1999, 12, 31, 23, 59, 59, 0, ZoneOffset.UTC);
        var later = ZonedDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);

        try (
                MockedStatic<UUID> uuidMock = mockStatic(UUID.class);
                MockedStatic<ZonedDateTime> timeMock = mockStatic(ZonedDateTime.class)
        ) {
            uuidMock.when(UUID::randomUUID).thenReturn(new UUID(0L, 0L));
            timeMock.when(ZonedDateTime::now).thenReturn(now);
            var original = new Configuration(mongo, 100, connectionProperties);
            target.add(original);

            var modified = new Configuration("00000000-0000-0000-0000-000000000000", questdb_ilp, 99, WAITING_FOR_TERMINATION, later, later, connectionProperties);

        //when
            target.update("00000000-0000-0000-0000-000000000000", modified);
        }

        //then
        Collection<Configuration> actual = target.all();
        assertThat(actual)
                .as("Check configuration was modified in storage")
                .filteredOn("id", "00000000-0000-0000-0000-000000000000")
                .filteredOn("target", questdb_ilp)
                .filteredOn("vehicles", 99)
                .filteredOn("status", WAITING_FOR_TERMINATION)
                .filteredOn("createdAt", later)
                .filteredOn("updatedAt", later)
                .filteredOn("connection.url", "url")
                .hasSize(1);

    }

}