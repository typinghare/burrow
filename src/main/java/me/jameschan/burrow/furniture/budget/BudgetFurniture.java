package me.jameschan.burrow.furniture.budget;

import me.jameschan.burrow.furniture.keyvalue.KeyValueFurniture;
import me.jameschan.burrow.furniture.time.TimeFurniture;
import me.jameschan.burrow.kernel.Chamber;
import me.jameschan.burrow.kernel.entry.Entry;
import me.jameschan.burrow.kernel.furniture.Furniture;
import me.jameschan.burrow.kernel.furniture.annotation.BurrowFurniture;

@BurrowFurniture(dependencies = {KeyValueFurniture.class, TimeFurniture.class})
public class BudgetFurniture extends Furniture {
  public BudgetFurniture(final Chamber chamber) {
    super(chamber);
  }

  @Override
  public void onCreateEntry(final Entry entry) {
    // The value has to be a valid floating point number
    final var value = entry.get(KeyValueFurniture.EntryKey.VALUE);
    try {
      final var amount = Float.parseFloat(value);
      entry.set(KeyValueFurniture.EntryKey.VALUE, String.format("%.2f", amount));
    } catch (final NumberFormatException ex) {
      throw new RuntimeException("Invalid amount string: " + value, ex);
    }
  }
}