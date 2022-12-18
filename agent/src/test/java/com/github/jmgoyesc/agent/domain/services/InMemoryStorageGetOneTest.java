package com.github.jmgoyesc.agent.domain.services;

import com.github.jmgoyesc.agent.domain.models.Configuration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;

import static com.github.jmgoyesc.agent.domain.models.Configuration.Status.CREATED;
import static com.github.jmgoyesc.agent.domain.models.Configuration.TargetDB.mongo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

/**
 * @author Juan Manuel Goyes Coral
 */

@DisplayName("UT. InMemoryStorageTest - get one")
@ExtendWith(MockitoExtension.class)
class InMemoryStorageGetOneTest {

    @InjectMocks private InMemoryStorage target;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @DisplayName("T1. GIVEN an existent configuration, WHEN get, THEN return existent configuration")
    @Test
    void test1() {
        //given
        var connectionProperties = new Configuration.ConnectionProperties("url");
        var now = ZonedDateTime.of(1999, 12, 31, 23, 59, 59, 0, ZoneOffset.UTC);

        try (
                MockedStatic<UUID> uuidMock = mockStatic(UUID.class);
                MockedStatic<ZonedDateTime> timeMock = mockStatic(ZonedDateTime.class)
        ) {
            uuidMock.when(UUID::randomUUID).thenReturn(new UUID(0L, 0L));
            timeMock.when(ZonedDateTime::now).thenReturn(now);
            var original = new Configuration(mongo, 100, connectionProperties);
            target.add(original);

        //when
            var actual = target.get("00000000-0000-0000-0000-000000000000");

        //then
        assertThat(actual)
                .as("Check configuration was found in storage")
                .hasFieldOrPropertyWithValue("id", "00000000-0000-0000-0000-000000000000")
                .hasFieldOrPropertyWithValue("target", mongo)
                .hasFieldOrPropertyWithValue("vehicles", 100)
                .hasFieldOrPropertyWithValue("status", CREATED)
                .hasFieldOrPropertyWithValue("createdAt", now)
                .hasFieldOrPropertyWithValue("updatedAt", now)
                .hasFieldOrPropertyWithValue("connection.url", "url");
        }

    }

    @DisplayName("T2. GIVEN empty storage, WHEN get, THEN throw not found")
    @Test
    void test2() {
        //given

        //when
        var actual = Assertions.assertThrows(IllegalArgumentException.class, () -> target.get("00000000-0000-0000-0000-000000000000"));

        //then
        Assertions.assertEquals("Configuration (id=00000000-0000-0000-0000-000000000000) does not exits", actual.getMessage());
    }

}