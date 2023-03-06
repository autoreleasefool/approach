import ComposableArchitecture

public protocol FeatureAction {
	associatedtype ViewAction
	associatedtype DelegateAction
	associatedtype InternalAction

	static func view(_: ViewAction) -> Self
	static func delegate(_: DelegateAction) -> Self
	static func `internal`(_: InternalAction) -> Self
}

extension Store where Action: FeatureAction {
	public func scope<ChildState, ChildAction>(
		state toChildState: @escaping (State) -> ChildState,
		action fromChildAction: CasePath<Action.InternalAction, ChildAction>
	) -> Store<ChildState, ChildAction> {
		scope(
			state: toChildState,
			action: { .internal(fromChildAction.embed($0)) }
		)
	}
}
