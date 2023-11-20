import AnalyticsServiceInterface
import ComposableArchitecture
import ErrorsFeature
import FeatureActionLibrary
import ProductsLibrary
import ProductsServiceInterface
import StringsLibrary

public struct Paywall: Reducer {
	public struct State: Equatable {
		public let product: Product

		public var isRestoringPurchases = false
		public var errors: Errors<ErrorID>.State = .init()

		public var isProductAvailable: Bool

		@BindingState public var isPaywallPresented: Bool = false

		public init(product: Product) {
			self.product = product

			@Dependency(\.products) var products
			self.isProductAvailable = products.peekIsAvailable(product)
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: BindableAction, Equatable {
			case onAppear
			case didStartTask
			case didTapRestorePurchasesButton
			case binding(BindingAction<State>)
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case setProductAvailability(Bool)
			case didFinishRestoringPurchases(TaskResult<Bool>)

			case errors(Errors<ErrorID>.Action)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public enum ErrorID: Hashable {
		case failedToRestorePurchases
	}

	public init() {}

	@Dependency(\.dismiss) var dismiss
	@Dependency(\.products) var products

	public var body: some ReducerOf<Self> {
		BindingReducer(action: /Action.view)

		Scope(state: \.errors, action: /Action.internal..Action.InternalAction.errors) {
			Errors()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .none

				case .didStartTask:
					return .run { [product = state.product] send in
						for try await isPurchased in self.products.observe(product) {
							await send(.internal(.setProductAvailability(isPurchased)))
						}
					}

				case .didTapRestorePurchasesButton:
					state.isRestoringPurchases = true
					return .run { send in
						try await products.restore()
						await send(.internal(.didFinishRestoringPurchases(.success(true))))
					} catch: { error, send in
						await send(.internal(.didFinishRestoringPurchases(.failure(error))))
					}

				case .binding:
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .setProductAvailability(available):
					state.isProductAvailable = available
					state.isPaywallPresented = !available && state.isPaywallPresented
					return .none

				case .didFinishRestoringPurchases(.success):
					state.isRestoringPurchases = false
					return .none

				case let .didFinishRestoringPurchases(.failure(error)):
					return state.errors
						.enqueue(.failedToRestorePurchases, thrownError: error, toastMessage: Strings.Error.Toast.failedToLoad)
						.map { .internal(.errors($0)) }

				case let .errors(.delegate(delegateAction)):
					switch delegateAction {
					case .never:
						return .none
					}

				case .errors(.internal), .errors(.view):
					return .none
				}

			case .delegate:
				return .none
			}
		}

		BreadcrumbReducer<State, Action> { _, action in
			switch action {
			case .view(.onAppear): return .navigationBreadcrumb(type(of: self))
			default: return nil
			}
		}
	}
}

extension Paywall.State {
	public mutating func presentPaywall() -> Effect<Paywall.Action> {
		isPaywallPresented = true
		return .none
	}
}
