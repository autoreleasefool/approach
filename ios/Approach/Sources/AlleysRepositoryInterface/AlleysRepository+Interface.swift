import Dependencies
import ModelsLibrary

extension Alley {
	public enum Ordering: Sendable {
		case byName
		case byRecentlyUsed

		public static let `default`: Self = .byRecentlyUsed
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
	public var mostRecentlyUsed: @Sendable (Int) -> AsyncThrowingStream<[Alley.Summary], Error>
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
		mostRecentlyUsed: @escaping @Sendable (Int) -> AsyncThrowingStream<[Alley.Summary], Error>,
		pickable: @escaping @Sendable () -> AsyncThrowingStream<[Alley.Summary], Error>,
		load: @escaping @Sendable (Alley.ID) -> AsyncThrowingStream<Alley.Summary, Error>,
		edit: @escaping @Sendable (Alley.ID) async throws -> Alley.EditWithLanes,
		create: @escaping @Sendable (Alley.Create) async throws -> Void,
		update: @escaping @Sendable (Alley.Edit) async throws -> Void,
		delete: @escaping @Sendable (Alley.ID) async throws -> Void
	) {
		self.list = list
		self.mostRecentlyUsed = mostRecentlyUsed
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

	public func mostRecent(limit: Int = 3) -> AsyncThrowingStream<[Alley.Summary], Error> {
		self.mostRecentlyUsed(limit)
	}
}

extension AlleysRepository: TestDependencyKey {
	public static var testValue: Self {
		Self(
			list: { _, _, _, _, _ in unimplemented("\(Self.self).list", placeholder: .never) },
			mostRecentlyUsed: { _ in unimplemented("\(Self.self).mostRecentlyUsed", placeholder: .never) },
			pickable: { unimplemented("\(Self.self).pickable", placeholder: .never) },
			load: { _ in unimplemented("\(Self.self).load", placeholder: .never) },
			edit: { _ in unimplemented("\(Self.self).edit", placeholder: .placeholder) },
			create: { _ in unimplemented("\(Self.self).create") },
			update: { _ in unimplemented("\(Self.self).update") },
			delete: { _ in unimplemented("\(Self.self).delete") }
		)
	}
}
