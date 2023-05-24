import Dependencies
import ModelsLibrary

extension Gear {
	public enum Ordering: Hashable, CaseIterable {
		case byName
		case byRecentlyUsed
	}
}

public struct GearRepository: Sendable {
	public var list: @Sendable (Bowler.ID?, Gear.Kind?, Gear.Ordering) -> AsyncThrowingStream<[Gear.Summary], Error>
	public var overview: @Sendable () -> AsyncThrowingStream<[Gear.Summary], Error>
	public var edit: @Sendable (Gear.ID) async throws -> Gear.Edit?
	public var create: @Sendable (Gear.Create) async throws -> Void
	public var update: @Sendable (Gear.Edit) async throws -> Void
	public var delete: @Sendable (Gear.ID) async throws -> Void

	public init(
		list: @escaping @Sendable (Bowler.ID?, Gear.Kind?, Gear.Ordering) -> AsyncThrowingStream<[Gear.Summary], Error>,
		overview: @escaping @Sendable () -> AsyncThrowingStream<[Gear.Summary], Error>,
		edit: @escaping @Sendable (Gear.ID) async throws -> Gear.Edit?,
		create: @escaping @Sendable (Gear.Create) async throws -> Void,
		update: @escaping @Sendable (Gear.Edit) async throws -> Void,
		delete: @escaping @Sendable (Gear.ID) async throws -> Void
	) {
		self.list = list
		self.overview = overview
		self.edit = edit
		self.create = create
		self.update = update
		self.delete = delete
	}

	public func list(
		ownedBy: Bowler.ID? = nil,
		ofKind: Gear.Kind? = nil,
		ordered: Gear.Ordering
	) -> AsyncThrowingStream<[Gear.Summary], Error> {
		self.list(ownedBy, ofKind, ordered)
	}
}

extension GearRepository: TestDependencyKey {
	public static var testValue = Self(
		list: { _, _, _ in unimplemented("\(Self.self).list") },
		overview: { unimplemented("\(Self.self).overview") },
		edit: { _ in unimplemented("\(Self.self).edit") },
		create: { _ in unimplemented("\(Self.self).create") },
		update: { _ in unimplemented("\(Self.self).update") },
		delete: { _ in unimplemented("\(Self.self).delete") }
	)
}

extension DependencyValues {
	public var gear: GearRepository {
		get { self[GearRepository.self] }
		set { self[GearRepository.self] = newValue }
	}
}
