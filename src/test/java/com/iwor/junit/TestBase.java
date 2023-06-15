package com.iwor.junit;

import com.iwor.junit.extension.GlobalExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({
        GlobalExtension.class,
})
public abstract class TestBase {
}
