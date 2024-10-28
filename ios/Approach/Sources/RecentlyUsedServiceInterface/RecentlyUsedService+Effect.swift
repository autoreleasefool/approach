import ComposableArchitecture
import Foundation

extension RecentlyUsedService {
	public func didRecentlyUse<R: Reducer>(_ resource: Resource, id: UUID, in _: R) -> EffectOf<R> {
		.run { _ in
			try? await Task.sleep(for: .seconds(1))
			self.didRecentlyUseResource(resource, id)
		}
	}
}
