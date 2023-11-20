import Dependencies

public struct StoreReviewService: Sendable {
	public var shouldRequestReview: @Sendable () -> Bool
	public var didRequestReview: @Sendable () async -> Void

	public init(
		shouldRequestReview: @escaping @Sendable () -> Bool,
		didRequestReview: @escaping @Sendable () async -> Void
	) {
		self.shouldRequestReview = shouldRequestReview
		self.didRequestReview = didRequestReview
	}
}

extension StoreReviewService: TestDependencyKey {
	public static var testValue = Self(
		shouldRequestReview: { unimplemented("\(Self.self).shouldRequestReview") },
		didRequestReview: { unimplemented("\(Self.self).didRequestReview") }
	)
}

extension DependencyValues {
	public var storeReview: StoreReviewService {
		get { self[StoreReviewService.self] }
		set { self[StoreReviewService.self] = newValue }
	}
}
