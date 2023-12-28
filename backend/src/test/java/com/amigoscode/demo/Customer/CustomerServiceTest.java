package com.amigoscode.demo.Customer;

import com.amigoscode.demo.exception.DuplicateResourceException;
import com.amigoscode.demo.exception.RequestValidationException;
import com.amigoscode.demo.exception.RessourceNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {


    @Mock
    private CustomerDao customerDao;
    private CustomerService underTest;

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDao);
    }

    @Test
    void getAllCustomers() {
        //When
        underTest.getAllCustomers();
        // Then
        verify(customerDao).selectAllCustomers();
    }

    @Test
    void canGetCustomer() {
        // Given
        int id =10;
        Customer customer = new Customer(id, "Alex","alex@gmail.com",19);
        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));
        // When
        Customer actual = underTest.getCustomer(10);
        // Then
        Assertions.assertThat(actual).isEqualTo(customer);
    }

    @Test
    void willThrowWhenGetCustomerReturnEmptyOptional() {
        // Given
        int id =10;

        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());
        // When
        // Then
        Assertions.assertThatThrownBy(() -> underTest.getCustomer(id))
                .isInstanceOf(RessourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found".formatted(id));
    }



    @Test
    void addCustomer() {
        // Given
        String email ="alex@gmail.com";

        Mockito.when(customerDao.existsPersonWithEmail(email)).thenReturn(false);

        // When
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Alex",email,19
        );
        underTest.addCustomer(request);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor= ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).insertCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();


        Assertions.assertThat(capturedCustomer.getId()).isNull();
        Assertions.assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        Assertions.assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        Assertions.assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
    }


    @Test
    void willThrowWhenEmailExistsWhileAddingACustomer() {
        // Given
        String email ="alex@gmail.com";

        Mockito.when(customerDao.existsPersonWithEmail(email)).thenReturn(true);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Alex",email,19
        );
        // When

        Assertions.assertThatThrownBy(() -> underTest.addCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("email already taken");

        // Then
        // verify that will ever execute insertCustomer()
        verify(customerDao , never()).insertCustomer(any());

    }
    @Test
    void deleteCustomerById() {
        // Given
        int id=1;
        Mockito.when(customerDao.existsPersonWithId(id)).thenReturn(true);

        //When
        underTest.deleteCustomerById(id);
        // Then
        verify(customerDao).deleteCustomerById(id);
    }

    @Test
    void willThrowDeleteCustomerByIdNotExists() {
        // Given
        int id=1;
        Mockito.when(customerDao.existsPersonWithId(id)).thenReturn(false);

        //When

        Assertions.assertThatThrownBy(()-> underTest.deleteCustomerById(id))
                .isInstanceOf(RessourceNotFoundException.class)
                .hasMessage("customer with id [%s] not found".formatted(id));
        // Then
        verify(customerDao , never()).deleteCustomerById(id);
    }

    @Test
    void canUpdateAllCustomersProperties() {
        //Given
        int id=10;
        Customer customer = new Customer(id,"Alex" , "alex@gmail.com",19);

        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                "Alexandro","alexandro@amigoscode.com",23);
        String newEmail ="alexandro@amigoscode.com";
        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        Mockito.when(customerDao.existsPersonWithEmail(newEmail)).thenReturn(false);
        // When
        underTest.updateCustomer(id, updateRequest);

        // Then
        ArgumentCaptor <Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        Assertions.assertThat(capturedCustomer.getName()).isEqualTo(updateRequest.name());
        Assertions.assertThat(capturedCustomer.getEmail()).isEqualTo(updateRequest.email());
        Assertions.assertThat(capturedCustomer.getAge()).isEqualTo(updateRequest.age());
    }

    @Test
    void canUpdateOnlyCustomerName() {
        //Given
        int id=10;
        Customer customer = new Customer(id,"Alex" , "alex@gmail.com",19);

        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                "Alexandro",null,null);
        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));


        // When
        underTest.updateCustomer(id, updateRequest);

        // Then
        ArgumentCaptor <Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        Assertions.assertThat(capturedCustomer.getName()).isEqualTo(updateRequest.name());
        Assertions.assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        Assertions.assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());

    }

    @Test
    void canUpdateOnlyCustomerEmail() {
        //Given
        int id=10;
        Customer customer = new Customer(id,"Alex" , "alex@gmail.com",19);

        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "alexandro@amigoscode.com";

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null,newEmail,null);

        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        Mockito.when(customerDao.existsPersonWithEmail(newEmail)).thenReturn(false);

        // When
        underTest.updateCustomer(id, updateRequest);

        // Then
        ArgumentCaptor <Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        Assertions.assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        Assertions.assertThat(capturedCustomer.getEmail()).isEqualTo(newEmail);
        Assertions.assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());

    }


    @Test
    void canUpdateOnlyCustomerAge() {
        //Given
        int id=10;
        Customer customer = new Customer(id,"Alex" , "alex@gmail.com",19);

        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null,null,22);
        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));


        // When
        underTest.updateCustomer(id, updateRequest);

        // Then
        ArgumentCaptor <Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        Assertions.assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        Assertions.assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        Assertions.assertThat(capturedCustomer.getAge()).isEqualTo(updateRequest.age());

    }

    @Test
    void willThrowWhenTryToUpdateCustomerEmailWhenAlreadyTaken() {
        //Given
        int id=10;
        Customer customer = new Customer(id,"Alex" , "alex@gmail.com",19);

        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "alexandro@amigoscode.com";

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                null,newEmail,null);

        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        Mockito.when(customerDao.existsPersonWithEmail(newEmail)).thenReturn(true);

        // When
        Assertions.assertThatThrownBy(()-> underTest.updateCustomer(id, updateRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Email already taken");

        // Then
        verify(customerDao, never()).updateCustomer(any());

    }

    @Test
    void willThrowWhenCustomerUpdateHasNoChanges() {
        //Given
        int id=10;
        Customer customer = new Customer(id,"Alex" , "alex@gmail.com",19);

        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                customer.getName(),customer.getEmail(), customer.getAge());

        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        // When
        Assertions.assertThatThrownBy(()-> underTest.updateCustomer(id, updateRequest))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("no data changes found");

        // Then
        verify(customerDao, never()).updateCustomer(any());

    }



}