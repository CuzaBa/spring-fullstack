package com.amigoscode.demo.Customer;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.sql.ResultSet;
import java.sql.SQLException;
import static org.mockito.Mockito.mock;

class CustomerRowMapperTest {

    @Test
    void mapRow() throws SQLException {
        // Given
        CustomerRowMapper customerRowMapper =  new CustomerRowMapper();
        // Mock ResultSet (we can use laso the anotation @mock)
        ResultSet resultSet  =  mock(ResultSet.class);
        Mockito.when(resultSet.getInt("id")).thenReturn(1);
        Mockito.when(resultSet.getInt("age")).thenReturn(19);
        Mockito.when(resultSet.getString("name")).thenReturn("Jamila");
        Mockito.when(resultSet.getString("email")).thenReturn("jamila@gmail.com");
        // When
        Customer actual = customerRowMapper.mapRow(resultSet,1);

        //  Then
        Customer expected = new Customer(1,"Jamila","jamila@gmail.com",19 );
        Assertions.assertThat(actual.getId()).isEqualTo(expected.getId());
        Assertions.assertThat(actual.getName()).isEqualTo(expected.getName());
        Assertions.assertThat(actual.getAge()).isEqualTo(expected.getAge());
        Assertions.assertThat(actual.getEmail()).isEqualTo(expected.getEmail());
    }
}