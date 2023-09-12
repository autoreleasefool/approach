import Dependencies
import ModelsLibrary

extension Alley {
	public enum Ordering {
		case byName
		case byRecentlyUsed
	}
}

public struct AlleysRepository: Sendable {
	public var list: @Sendable (
		Alley.Material?,
		Alley.PinFall?,
		Alley.Mechanism?,
		Alley.PinBase?,
		Alley.Ordering
	) -> AsyncThrowingStream<[Alley.List], Error>
	public var overview: @Sendable () -> AsyncThrowingStream<[Alley.Summary], Error>
	public var pickable: @Sendable () -> AsyncThrowingStream<[Alley.Summary], Error>
	public var load: @Sendable (Alley.ID) -> AsyncThrowingStream<Alley.Summary, Error>
	public var edit: @Sendable (Alley.ID) async throws -> Alley.EditWithLanes
	public var create: @Sendable (Alley.Create) async throws -> Void
	public var update: @Sendable (Alley.Edit) async throws -> Void
	public var delete: @Sendable (Alley.ID) async throws -> Void

	public init(
		list: @escaping @Sendable (
			Alley.Material?,
			Alley.PinFall?,
			Alley.Mechanism?,
			Alley.PinBase?,
			Alley.Ordering
		) -> AsyncThrowingStream<[Alley.List], Error>,
		overview: @escaping @Sendable () -> AsyncThrowingStream<[Alley.Summary], Error>,
		pickable: @escaping @Sendable () -> AsyncThrowingStream<[Alley.Summary], Error>,
		load: @escaping @Sendable (Alley.ID) -> AsyncThrowingStream<Alley.Summary, Error>,
		edit: @escaping @Sendable (Alley.ID) async throws -> Alley.EditWithLanes,
		create: @escaping @Sendable (Alley.Create) async throws -> Void,
		update: @escaping @Sendable (Alley.Edit) async throws -> Void,
		delete: @escaping @Sendable (Alley.ID) async throws -> Void
	) {
		self.list = list
		self.overview = overview
		self.pickable = pickable
		self.load = load
		self.edit = edit
		self.create = create
		self.update = update
		self.delete = delete
	}

	public func list(ordered: Alley.Ordering) -> AsyncThrowingStream<[Alley.List], Error> {
		self.list(nil, nil, nil, nil, ordered)
	}

	public func filteredList(
		withMaterial: Alley.Material? = nil,
		withPinFall: Alley.PinFall? = nil,
		withMechanism: Alley.Mechanism? = nil,
		withPinBase: Alley.PinBase? = nil,
		ordered: Alley.Ordering
	) -> AsyncThrowingStream<[Alley.List], Error> {
		self.list(withMaterial, withPinFall, withMechanism, withPinBase, ordered)
	}
}

extension AlleysRepository: TestDependencyKey {
	public static var testValue = Self(
		list: { _, _, _, _, _ in unimplemented("\(Self.self).list") },
		overview: { unimplemented("\(Self.self).overview") },
		pickable: { unimplemented("\(Self.self).pickable") },
		load: { _ in unimplemented("\(Self.self).load") },
		edit: { _ in unimplemented("\(Self.self).edit") },
		create: { _ in unimplemented("\(Self.self).create") },
		update: { _ in unimplemented("\(Self.self).update") },
		delete: { _ in unimplemented("\(Self.self).delete") }
	)
}

extension DependencyValues {
	public var alleys: AlleysRepository {
		get { self[AlleysRepository.self] }
		set { self[AlleysRepository.self] = newValue }
	}
}
