package name.joseland.mal.automation.core;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.function.*;

import static org.junit.Assert.assertEquals;

public class GenericEntityTester<T> {

    private final Supplier<T> entityInitialiser;
    private final JpaRepository<T, Integer> repository;

    private final List<EntityTestedField<?>> entityTestedFields;

    private GenericEntityTester(Supplier<T> entityInitialiser, JpaRepository<T, Integer> repository) {
        this.entityInitialiser = entityInitialiser;
        this.repository = repository;

        this.entityTestedFields = new ArrayList<>();
    }

    public static <U> GenericEntityTester<U> buildNew(Supplier<U> entityInitialiser,
                                                      JpaRepository<U, Integer> repository) {
        return new GenericEntityTester<>(entityInitialiser, repository);
    }

    public <U> void addField(U testValue, U updateTestValue, Function<T, U> getter, BiConsumer<T, U> setter) {
        EntityTestedField<U> newTestedField = new EntityTestedField<>(testValue, updateTestValue, getter, setter);
        entityTestedFields.add(newTestedField);
    }


    /**
     * Tests save for entity T, including all fields in entityTestedFields.
     */
    public void saveRetrieve() {
        // save new test instance
        T instance = getNewTestInstance();
        repository.save(instance);

        // find saved instances, assert 1
        List<T> retrievedInstances = repository.findAll();
        assertEquals(1, retrievedInstances.size());

        // test that field test values are equal to the values on the retrieved instance
        T retrievedInstance = retrievedInstances.get(0);
        entityTestedFields.forEach(entityTestedField ->
                assertEquals(entityTestedField.testValue, entityTestedField.getter.apply(retrievedInstance)));
    }

    /**
     * Tests delete for entity T.
     */
    public void delete() {
        // save new test instance
        T instance = getNewTestInstance();
        repository.save(instance);

        // find saved instances, assert 1
        List<T> retrievedInstances = repository.findAll();
        assertEquals(1, retrievedInstances.size());

        // delete the retrieved instance
        T retrievedInstance = retrievedInstances.get(0);
        repository.delete(retrievedInstance);

        // assert 0 saved instances
        List<T> retrievedInstancesAfterDelete = repository.findAll();
        assertEquals(0, retrievedInstancesAfterDelete.size());
    }


    /**
     * Tests update on all fields for an initialised entity with type T.
     */
    public void update() {
        // save new test instance
        T instance = getNewTestInstance();
        repository.save(instance);

        // find saved instances, assert 1
        List<T> retrivedInstances = repository.findAll();
        assertEquals(1, retrivedInstances.size());

        // for each field; set updateTestValue to the retrieved instance
        T retrievedInstance = retrivedInstances.get(0);
        entityTestedFields.forEach(entityTestedField ->
                entityTestedField.setUpdateTestValueToInstance(retrievedInstance));
        repository.save(retrievedInstance);

        // find saved instances, assert 1
        List<T> retrivedInstancesAfterUpdate = repository.findAll();
        assertEquals(1, retrivedInstances.size());

        // assert all fields set to updateTestValue
        T retrievedInstanceAfterUpdate = retrivedInstancesAfterUpdate.get(0);
        entityTestedFields.forEach(entityTestedField ->
                assertEquals(entityTestedField.updateTestValue,
                        entityTestedField.getter.apply(retrievedInstanceAfterUpdate)));
    }

    private T getNewTestInstance() {
        T testEntity = entityInitialiser.get();

        entityTestedFields.forEach(entityTestedField -> entityTestedField.setTestValueToInstance(testEntity));

        return testEntity;
    }


    public class EntityTestedField<U> {
        private final U testValue;
        private final U updateTestValue;

        private final Function<T, U> getter;
        private final BiConsumer<T, U> setter;

        EntityTestedField(U testValue, U updateTestValue, Function<T, U> getter, BiConsumer<T, U> setter) {
            this.testValue = testValue;
            this.updateTestValue = updateTestValue;
            this.getter = getter;
            this.setter = setter;
        }

        private void setTestValueToInstance(T instance) {
            setter.accept(instance, testValue);
        }

        private void setUpdateTestValueToInstance(T instance) {
            setter.accept(instance, updateTestValue);
        }

    }

}
