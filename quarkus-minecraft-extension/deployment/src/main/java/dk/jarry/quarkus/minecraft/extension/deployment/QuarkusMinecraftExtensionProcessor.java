package dk.jarry.quarkus.minecraft.extension.deployment;

import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.AnnotationsTransformerBuildItem;
import io.quarkus.arc.deployment.BeanContainerBuildItem;
import io.quarkus.arc.processor.AnnotationsTransformer;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.LogHandlerBuildItem;
import io.quarkus.resteasy.reactive.spi.ExceptionMapperBuildItem;

import org.jboss.jandex.DotName;

import dk.jarry.quarkus.minecraft.runtime.HelloRecorder;
import dk.jarry.quarkus.minecraft.runtime.MinecraftLog;
import dk.jarry.quarkus.minecraft.runtime.MinecraftLogHandlerMaker;
import dk.jarry.quarkus.minecraft.runtime.MinecraftLogInterceptor;
import dk.jarry.quarkus.minecraft.runtime.MinecraftService;
import dk.jarry.quarkus.minecraft.runtime.RestExceptionMapper;

import javax.ws.rs.Priorities;

import static io.quarkus.deployment.annotations.ExecutionTime.RUNTIME_INIT;
import static io.quarkus.deployment.annotations.ExecutionTime.STATIC_INIT;

class QuarkusMinecraftExtensionProcessor {

    private static final String FEATURE = "quarkus-minecraft-extension";
    private static final DotName JAX_RS_GET = DotName.createSimple("javax.ws.rs.GET");

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @Record(STATIC_INIT)
    @BuildStep
    public void helloBuildStep(HelloRecorder recorder) {
        recorder.sayHello("World");
    }

    @Record(RUNTIME_INIT)
    @BuildStep
    LogHandlerBuildItem addLogHandler(final MinecraftLogHandlerMaker maker, BeanContainerBuildItem beanContainer) {
        return new LogHandlerBuildItem(maker.create(beanContainer.getValue()));
    }

    /**
     * Makes the interceptor as a bean so we can access it.
     */
    @BuildStep
    void beans(BuildProducer<AdditionalBeanBuildItem> producer) {
        producer.produce(AdditionalBeanBuildItem.unremovableOf(MinecraftLogInterceptor.class));
        producer.produce(AdditionalBeanBuildItem.unremovableOf(MinecraftService.class));
    }

    @BuildStep
    AnnotationsTransformerBuildItem transform() {
        return new AnnotationsTransformerBuildItem(new AnnotationsTransformer() {

            public boolean appliesTo(org.jboss.jandex.AnnotationTarget.Kind kind) {
                return kind == org.jboss.jandex.AnnotationTarget.Kind.METHOD;
            }

            public void transform(TransformationContext context) {
                if (context.getTarget().asMethod().hasAnnotation(JAX_RS_GET)) {
                    context.transform().add(MinecraftLog.class).done();
                }
            }
        });
    }

    @BuildStep
    ExceptionMapperBuildItem exceptionMappers() {
        return new ExceptionMapperBuildItem(RestExceptionMapper.class.getName(),
                Exception.class.getName(), Priorities.USER + 100, true);
    }

}
