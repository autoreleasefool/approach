import AnalyticsPackageServiceInterface
import ComposableArchitecture

@Reducer
public struct BreadcrumbReducer<State, Action>: Reducer {
	let reducer: (State, Action) -> Breadcrumb?

	public init(reducer: @escaping (_ state: State, _ action: Action) -> Breadcrumb?) {
		self.reducer = reducer
	}

	@Dependency(\.breadcrumbs) var breadcrumbs

	public var body: some Reducer<State, Action> {
		Reduce { state, action in
			guard let breadcrumb = reducer(state, action) else { return .none }
			return .run { _ in await breadcrumbs.drop(breadcrumb) }
		}
	}
}
