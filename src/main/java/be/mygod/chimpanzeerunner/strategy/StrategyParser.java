package be.mygod.chimpanzeerunner.strategy;

import be.mygod.chimpanzeerunner.test.TestManager;
import com.beust.jcommander.IStringConverter;

import java.util.function.Function;

public class StrategyParser implements IStringConverter<Function<TestManager, AbstractStrategy>> {
    @Override
    public Function<TestManager, AbstractStrategy> convert(String value) {
        switch (value.toLowerCase()) {
            case "random": case "": return RandomSelectionStrategy::new;
            default: return null;
        }
    }
}
