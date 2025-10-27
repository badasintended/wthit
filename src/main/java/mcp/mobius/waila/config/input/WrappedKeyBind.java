package mcp.mobius.waila.config.input;

import java.util.function.Consumer;
import java.util.function.Supplier;

import mcp.mobius.waila.api.WailaConstants;
import mcp.mobius.waila.mixed.MWrappedKeyBind;
import mcp.mobius.waila.mixin.KeyMappingAccess;
import net.minecraft.client.KeyMapping;

public class WrappedKeyBind extends KeyMapping implements MWrappedKeyBind {

    private final Consumer<KeyBind> setter;

    public WrappedKeyBind(String desc, KeyBind defaultKey, Consumer<KeyBind> setter, Supplier<KeyBind> getter) {
        super(desc, defaultKey.key().getValue(), WailaConstants.MOD_NAME);
        this.setter = setter;
        setKey(getter.get().key());
        KeyMappingAccess.wthit_all().remove(desc);
    }

    @Override
    public boolean same(KeyMapping keyMapping) {
        return false;
    }

    @Override
    public void update() {
        setter.accept(KeyBind.of(((KeyMappingAccess) this).wthit_key()));
    }

}
