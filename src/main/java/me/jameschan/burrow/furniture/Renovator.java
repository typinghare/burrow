package me.jameschan.burrow.furniture;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import me.jameschan.burrow.chamber.Chamber;
import me.jameschan.burrow.chamber.ChamberBased;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Renovator extends ChamberBased {
  private final Map<String, Furniture> byName = new LinkedHashMap<>();

  public Renovator(final Chamber chamber) {
    super(chamber);
  }

  public Class<? extends Furniture> checkIfFurnitureExist(final String name) {
    try {
      @SuppressWarnings("unchecked")
      final var clazz = (Class<? extends Furniture>) Class.forName(name);
      return clazz;
    } catch (final ClassNotFoundException ex) {
      throw new RuntimeException(ex);
    }
  }

  public void testFurnitureClass(final Class<? extends Furniture> clazz) {
    final String name = clazz.getName();
    if (!Furniture.class.isAssignableFrom(clazz)) {
      throw new ClassCastException("Class does not extend Furniture: " + name);
    }
  }

  public void loadByName(final String name) {
    final var clazz = checkIfFurnitureExist(name);
    loadByClass(clazz);
  }

  public void loadByClass(final Class<? extends Furniture> clazz) {
    testFurnitureClass(clazz);

    Constructor<? extends Furniture> constructor;
    try {
      constructor = clazz.getDeclaredConstructor(Chamber.class);
    } catch (final NoSuchMethodException ex) {
      throw new RuntimeException(ex);
    }

    try {
      constructor.setAccessible(true);
      final var furniture = constructor.newInstance(getChamber());
      byName.put(clazz.getName(), furniture);
    } catch (InstantiationException
        | IllegalArgumentException
        | InvocationTargetException
        | IllegalAccessException ex) {
      throw new RuntimeException("Failed to instantiate furniture: " + clazz.getName(), ex);
    }
  }

  public Collection<Furniture> getAllFurniture() {
    return byName.values();
  }

  public <T extends Furniture> T getFurniture(final Class<T> clazz) {
    @SuppressWarnings("unchecked")
    final var furniture = (T) byName.get(clazz.getName());
    return furniture;
  }
}
