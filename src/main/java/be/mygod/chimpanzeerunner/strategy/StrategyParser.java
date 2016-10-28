package be.mygod.chimpanzeerunner.strategy;

import be.mygod.chimpanzeerunner.test.TestManager;
import com.beust.jcommander.IStringConverter;

import java.util.function.Function;

public class StrategyParser implements IStringConverter<Function<TestManager, AbstractStrategy>> {
    @Override
    public Function<TestManager, AbstractStrategy> convert(String value) {
        switch (value.toLowerCase()) {
            case "random": return RandomSelectionStrategy::new;
            case "random-bias": return RandomBiasSelectionStrategy::new;
            default: return InvalidStrategy::new;
        }
    }
}
