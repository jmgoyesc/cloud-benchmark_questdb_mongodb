package com.github.jmgoyesc.agent.domain.services;

import com.github.jmgoyesc.agent.domain.models.config.Configuration;
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

import static com.github.jmgoyesc.agent.domain.models.config.Configuration.Status.CREATED;
import static com.github.jmgoyesc.agent.domain.models.config.TargetDB.mongo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

/**
 * @author Juan Manuel Goyes Coral
 */

@DisplayName("UT. InMemoryStorageTest - add")
@ExtendWith(MockitoExtension.class)
class InMemoryStorageAddTest {

    @InjectMocks private InMemoryStorage target;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @DisplayName("T1. GIVEN a configuration, WHEN add, THEN add new configuration to storage")
    @Test
    void test1() {
        //given
        var id = UUID.fromString("00000000-0000-0000-0000-000000000000");
        var connectionProperties = new Configuration.ConnectionProperties("url");
        var now = ZonedDateTime.of(1999, 12, 31, 23, 59, 59, 0, ZoneOffset.UTC);

        Configuration configuration;
        try (
                MockedStatic<UUID> uuidMock = mockStatic(UUID.class);
                MockedStatic<ZonedDateTime> timeMock = mockStatic(ZonedDateTime.class)
        ) {
            uuidMock.when(UUID::randomUUID).thenReturn(id);
            timeMock.when(ZonedDateTime::now).thenReturn(now);
            configuration = Configuration.builder()
                    .target(mongo)
                    .vehicles(100)
                    .connection(connectionProperties)
                    .build();

            //when
            target.add(configuration, null);
        }

        //then
        Collection<Configuration> actual = target.all();
        assertThat(actual)
                .as("Check configuration was added to storage")
                .filteredOn("id", "00000000-0000-0000-0000-000000000000")
                .filteredOn("target", mongo)
                .filteredOn("vehicles", 100)
                .filteredOn("status", CREATED)
                .filteredOn("createdAt", now)
                .filteredOn("updatedAt", now)
                .filteredOn("connection.url", "url")
                .hasSize(1);

    }

}