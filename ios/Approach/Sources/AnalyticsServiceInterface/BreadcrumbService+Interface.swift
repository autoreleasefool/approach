import Dependencies
import DependenciesMacros
import Foundation

@DependencyClient
public struct BreadcrumbService: Sendable {
	public var drop: @Sendable (Breadcrumb) async -> Void
}

extension BreadcrumbService: TestDependencyKey {
	public static var testValue: Self { Self() }
}

extension DependencyValues {
	public var breadcrumbs: BreadcrumbService {
		get { self[BreadcrumbService.self] }
		set { self[BreadcrumbService.self] = newValue }
	}
}
