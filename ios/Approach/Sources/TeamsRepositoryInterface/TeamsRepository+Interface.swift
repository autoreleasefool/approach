import Dependencies
import ModelsLibrary

extension Team {
	public enum Ordering: String, Hashable, CaseIterable, Sendable {
		case byName
		case byRecentlyUsed
	}
}

public struct TeamsRepository: Sendable {
	public var list: @Sendable (Team.Ordering) -> AsyncThrowingStream<[Team.List], Error>

	public init(
		list: @escaping @Sendable (Team.Ordering) -> AsyncThrowingStream<[Team.List], Error>
	) {
		self.list = list
	}

	public func list(ordered: Team.Ordering) -> AsyncThrowingStream<[Team.List], Error> {
		self.list(ordered)
	}
}

extension TeamsRepository: TestDependencyKey {
	public static var testValue: Self {
		Self(
			list: { _ in unimplemented("\(Self.self).list", placeholder: .never) }
		)
	}
}
