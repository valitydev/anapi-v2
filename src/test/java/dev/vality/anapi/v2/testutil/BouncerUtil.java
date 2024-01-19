package dev.vality.anapi.v2.testutil;

import dev.vality.bouncer.ctx.ContextFragment;
import dev.vality.bouncer.decisions.Judgement;
import dev.vality.bouncer.decisions.Resolution;
import dev.vality.bouncer.decisions.ResolutionAllowed;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.thrift.TSerializer;

@UtilityClass
public class BouncerUtil {

    @SneakyThrows
    public static ContextFragment createContextFragment() {
        ContextFragment fragment = DamselUtil.fillRequiredTBaseObject(new ContextFragment(), ContextFragment.class);
        fragment.setContent(new TSerializer().serialize(new dev.vality.bouncer.context.v1.ContextFragment()));
        return fragment;
    }

    public static Judgement createJudgementAllowed() {
        Resolution resolution = new Resolution();
        resolution.setAllowed(new ResolutionAllowed());
        return new Judgement().setResolution(resolution);
    }
}
