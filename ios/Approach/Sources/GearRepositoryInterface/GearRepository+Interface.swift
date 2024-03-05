import Dependencies
import ModelsLibrary

extension Gear {
	public enum Ordering: Hashable, CaseIterable {
		case byName
		case byRecentlyUsed

		public static var `default`: Self = .byRecentlyUsed
	}
}

public struct GearRepository: Sendable {
	public var list: @Sendable (Bowler.ID?, Gear.Kind?, Gear.Ordering) -> AsyncThrowingStream<[Gear.Summary], Error>
	public var preferred: @Sendable (Bowler.ID) async throws -> [Gear.Summary]
	public var mostRecentlyUsed: @Sendable (Gear.Kind?, Int) -> AsyncThrowingStream<[Gear.Summary], Error>
	public var edit: @Sendable (Gear.ID) async throws -> Gear.Edit
	public var create: @Sendable (Gear.Create) async throws -> Void
	public var update: @Sendable (Gear.Edit) async throws -> Void
	public var delete: @Sendable (Gear.ID) async throws -> Void
	public var updatePreferredGear: @Sendable (Bowler.ID, [Gear.ID]) async throws -> Void

	public init(
		list: @escaping @Sendable (Bowler.ID?, Gear.Kind?, Gear.Ordering) -> AsyncThrowingStream<[Gear.Summary], Error>,
		preferred: @escaping @Sendable (Bowler.ID) async throws -> [Gear.Summary],
		mostRecentlyUsed: @escaping @Sendable (Gear.Kind?, Int) -> AsyncThrowingStream<[Gear.Summary], Error>,
		edit: @escaping @Sendable (Gear.ID) async throws -> Gear.Edit,
		create: @escaping @Sendable (Gear.Create) async throws -> Void,
		update: @escaping @Sendable (Gear.Edit) async throws -> Void,
		delete: @escaping @Sendable (Gear.ID) async throws -> Void,
		updatePreferredGear: @escaping @Sendable (Bowler.ID, [Gear.ID]) async throws -> Void
	) {
		self.list = list
		self.preferred = preferred
		self.mostRecentlyUsed = mostRecentlyUsed
		self.edit = edit
		self.create = create
		self.update = update
		self.delete = delete
		self.updatePreferredGear = updatePreferredGear
	}

	public func list(
		ownedBy: Bowler.ID? = nil,
		ofKind: Gear.Kind? = nil,
		ordered: Gear.Ordering
	) -> AsyncThrowingStream<[Gear.Summary], Error> {
		self.list(ownedBy, ofKind, ordered)
	}

	public func mostRecentlyUsed(
		ofKind kind: Gear.Kind? = nil,
		limit: Int = 3
	) -> AsyncThrowingStream<[Gear.Summary], Error> {
		self.mostRecentlyUsed(kind, limit)
	}

	public func preferredGear(forBowler: Bowler.ID) async throws -> [Gear.Summary] {
		try await self.preferred(forBowler)
	}

	public func updatePreferredGear(_ gear: [Gear.ID], forBowler: Bowler.ID) async throws {
		try await self.updatePreferredGear(forBowler, gear)
	}
}

extension GearRepository: TestDependencyKey {
	public static var testValue = Self(
		list: { _, _, _ in unimplemented("\(Self.self).list") },
		preferred: { _ in unimplemented("\(Self.self).preferred") },
		mostRecentlyUsed: { _, _ in unimplemented("\(Self.self).mostRecentlyUsed") },
		edit: { _ in unimplemented("\(Self.self).edit") },
		create: { _ in unimplemented("\(Self.self).create") },
		update: { _ in unimplemented("\(Self.self).update") },
		delete: { _ in unimplemented("\(Self.self).delete") },
		updatePreferredGear: { _, _ in unimplemented("\(Self.self).updatePreferredGear") }
	)
}
