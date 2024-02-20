import ComposableArchitecture

public protocol FeatureAction {
	associatedtype View
	associatedtype Delegate
	associatedtype Internal

	static func view(_: View) -> Self
	static func delegate(_: Delegate) -> Self
	static func `internal`(_: Internal) -> Self
}
