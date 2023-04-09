import Dependencies
import ModelsLibrary

public struct AlleysRepository: Sendable {
	public var list: @Sendable (Alley.FetchRequest) -> AsyncThrowingStream<[Alley.Summary], Error>
	public var load: @Sendable (Alley.ID) -> AsyncThrowingStream<Alley.Summary, Error>
	public var edit: @Sendable (Alley.ID) async throws -> Alley.Editable?
	public var save: @Sendable (Alley.Editable) async throws -> Void
	public var delete: @Sendable (Alley.ID) async throws -> Void

	public init(
		list: @escaping @Sendable (Alley.FetchRequest) -> AsyncThrowingStream<[Alley.Summary], Error>,
		load: @escaping @Sendable (Alley.ID) -> AsyncThrowingStream<Alley.Summary, Error>,
		edit: @escaping @Sendable (Alley.ID) async throws -> Alley.Editable?,
		save: @escaping @Sendable (Alley.Editable) async throws -> Void,
		delete: @escaping @Sendable (Alley.ID) async throws -> Void
	) {
		self.list = list
		self.load = load
		self.edit = edit
		self.save = save
		self.delete = delete
	}
}

extension AlleysRepository: TestDependencyKey {
	public static var testValue = Self(
		list: { _ in unimplemented("\(Self.self).list") },
		load: { _ in unimplemented("\(Self.self).load") },
		edit: { _ in unimplemented("\(Self.self).edit") },
		save: { _ in unimplemented("\(Self.self).save") },
		delete: { _ in unimplemented("\(Self.self).delete") }
	)
}

extension DependencyValues {
	public var alleys: AlleysRepository {
		get { self[AlleysRepository.self] }
		set { self[AlleysRepository.self] = newValue }
	}
}
