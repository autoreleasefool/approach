import Dependencies
import ModelsLibrary

public struct LeaguesRepository: Sendable {
	public var list: @Sendable (League.FetchRequest) -> AsyncThrowingStream<[League.Summary], Error>
	public var edit: @Sendable (League.ID) async throws -> League.Editable?
	public var save: @Sendable (League.Editable) async throws -> Void
	public var delete: @Sendable (League.ID) async throws -> Void

	public init(
		list: @escaping @Sendable (League.FetchRequest) -> AsyncThrowingStream<[League.Summary], Error>,
		edit: @escaping @Sendable (League.ID) async throws -> League.Editable?,
		save: @escaping @Sendable (League.Editable) async throws -> Void,
		delete: @escaping @Sendable (League.ID) async throws -> Void
	) {
		self.list = list
		self.edit = edit
		self.save = save
		self.delete = delete
	}
}

extension LeaguesRepository: TestDependencyKey {
	public static var testValue = Self(
		list: { _ in unimplemented("\(Self.self).list") },
		edit: { _ in unimplemented("\(Self.self).edit") },
		save: { _ in unimplemented("\(Self.self).save") },
		delete: { _ in unimplemented("\(Self.self).delete") }
	)
}

extension DependencyValues {
	public var leagues: LeaguesRepository {
		get { self[LeaguesRepository.self] }
		set { self[LeaguesRepository.self] = newValue }
	}
}
