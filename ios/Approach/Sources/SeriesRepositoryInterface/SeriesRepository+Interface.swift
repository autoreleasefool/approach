import Dependencies
import ModelsLibrary

extension Series {
	public enum Ordering: Hashable, CaseIterable {
		case byDate
	}
}

public struct SeriesRepository: Sendable {
	public var list: @Sendable (League.ID, Series.Ordering) -> AsyncThrowingStream<[Series.Summary], Error>
	public var edit: @Sendable (Series.ID) async throws -> Series.Editable?
	public var save: @Sendable (Series.Editable) async throws -> Void
	public var delete: @Sendable (Series.ID) async throws -> Void

	public init(
		list: @escaping @Sendable (League.ID, Series.Ordering) -> AsyncThrowingStream<[Series.Summary], Error>,
		edit: @escaping @Sendable (Series.ID) async throws -> Series.Editable?,
		save: @escaping @Sendable (Series.Editable) async throws -> Void,
		delete: @escaping @Sendable (Series.ID) async throws -> Void
	) {
		self.list = list
		self.edit = edit
		self.save = save
		self.delete = delete
	}

	public func list(bowledIn: League.ID, ordering: Series.Ordering) -> AsyncThrowingStream<[Series.Summary], Error> {
		self.list(bowledIn, ordering)
	}
}

extension SeriesRepository: TestDependencyKey {
	public static var testValue = Self(
		list: { _, _ in unimplemented("\(Self.self).list") },
		edit: { _ in unimplemented("\(Self.self).edit") },
		save: { _ in unimplemented("\(Self.self).save") },
		delete: { _ in unimplemented("\(Self.self).delete") }
	)
}

extension DependencyValues {
	public var series: SeriesRepository {
		get { self[SeriesRepository.self] }
		set { self[SeriesRepository.self] = newValue }
	}
}
