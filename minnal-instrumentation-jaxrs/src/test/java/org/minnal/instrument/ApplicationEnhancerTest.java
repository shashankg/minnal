/**
 * 
 */
package org.minnal.instrument;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.util.Set;

import javax.persistence.Entity;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;

import org.minnal.core.scanner.Scanner;
import org.minnal.core.scanner.Scanner.Listener;
import org.minnal.instrument.entity.AggregateRoot;
import org.minnal.instrument.resource.ResourceEnhancer;
import org.minnal.instrument.resource.metadata.ResourceMetaData;
import org.minnal.instrument.resource.metadata.ResourceMetaDataProvider;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @author ganeshs
 *
 */
public class ApplicationEnhancerTest {
	
	private Application application;
	
	private ResourceMetaData rc1;
	
	private ResourceMetaData rc2;
	
	private ApplicationEnhancer enhancer;
	
	private NamingStrategy namingStrategy;
	
	@BeforeMethod
	public void setup() {
		application = mock(Application.class);
		rc1 = ResourceMetaDataProvider.instance().getResourceMetaData(DummyResource.class);
		rc2 = ResourceMetaDataProvider.instance().getResourceMetaData(DummyResource1.class);
		when(application.getClasses()).thenReturn(Sets.newHashSet(DummyResource.class, DummyResource1.class));
		namingStrategy = new DefaultNamingStrategy();
		enhancer = spy(new ApplicationEnhancer(application, namingStrategy, new String[]{"org.minnal.instrument"}));
	}

	@Test
	public void shouldEnhanceResourceClass() {
		doReturn(Lists.newArrayList(DummyModel.class)).when(enhancer).scanClasses(any(Scanner.class));
		doReturn(Sets.newHashSet(rc2)).when(enhancer).getDefinedResources();
		ResourceEnhancer resEnhancer1 = mock(ResourceEnhancer.class);
		doReturn(resEnhancer1).when(enhancer).createEnhancer(null, DummyModel.class);
		enhancer.enhance();
		verify(resEnhancer1).enhance();
	}
	
	@Test
	public void shouldEnhanceResourceClassIgnoringEntityClassWithSamePath() {
		doReturn(Lists.newArrayList(DummyModel.class)).when(enhancer).scanClasses(any(Scanner.class));
		doReturn(Sets.newHashSet(rc1)).when(enhancer).getDefinedResources();
		ResourceEnhancer resEnhancer1 = mock(ResourceEnhancer.class);
		doReturn(resEnhancer1).when(enhancer).createEnhancer(rc1, DummyModel.class);
		enhancer.enhance();
		verify(resEnhancer1).enhance();
	}
	
	@Test
	public void shouldEnhanceResourceClassIgnoringResourceClassNotMatchingEntityPath() {
		doReturn(Lists.newArrayList(DummyModel.class)).when(enhancer).scanClasses(any(Scanner.class));
		doReturn(Sets.newHashSet(rc2)).when(enhancer).getDefinedResources();
		ResourceEnhancer resEnhancer1 = mock(ResourceEnhancer.class);
		ResourceEnhancer resEnhancer2 = mock(ResourceEnhancer.class);
		doReturn(resEnhancer1).when(enhancer).createEnhancer(null, DummyModel.class);
		doReturn(resEnhancer2).when(enhancer).createEnhancer(rc2, null);
		enhancer.enhance();
		verify(resEnhancer1).enhance();
		verify(resEnhancer2, never()).enhance();
	}
	
	@Test
	public void shouldGetDefinedResources() {
		Set<ResourceMetaData> resources = enhancer.getDefinedResources();
		assertEquals(resources.size(), 2);
	}
	
	@Test
	public void shouldGetDefinedResourcesIncludingSingletons() {
		when(application.getSingletons()).thenReturn(Sets.<Object>newHashSet(new DummyResource2()));
		Set<ResourceMetaData> resources = enhancer.getDefinedResources();
		assertEquals(resources.size(), 3);
	}
	
	@Test
	public void shouldGetDefinedResourcesExcludingResourcesWithoutPathAnnotation() {
		when(application.getSingletons()).thenReturn(Sets.<Object>newHashSet(new DummyResource2(), new DummyResource3()));
		Set<ResourceMetaData> resources = enhancer.getDefinedResources();
		assertEquals(resources.size(), 3);
	}
	
	@Test
	public void shouldScanClasses() {
		Scanner<Class<?>> scanner = mock(Scanner.class);
		enhancer.scanClasses(scanner);
		verify(scanner).scan(any(Listener.class));
	}
	
	@Path("/dummy_models")
	class DummyResource {
		@GET
		public void get() {}
	}
	
	@Path("/dummy1")
	class DummyResource1 {
		@GET
		public void get() {}
	}
	
	@Path("/dummy2")
	class DummyResource2 {
		@GET
		public void get() {}
	}
	
	class DummyResource3 {
		@GET
		public void get() {}
	}
	
	@Entity
	@AggregateRoot
	class DummyModel {
	}
	
	static class TestNamingStrategy extends DefaultNamingStrategy {
	}
}
