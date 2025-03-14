import AnalyticsServiceInterface
import ComposableArchitecture
import ErrorsFeature
import FeatureActionLibrary
import ProductsLibrary
import ProductsServiceInterface
import StringsLibrary

@Reducer
public struct Paywall: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		public let product: Product

		public var isRestoringPurchases = false
		public var errors: Errors<ErrorID>.State = .init()

		public var isProductAvailable: Bool

		public var isPaywallPresented: Bool = false

		public init(product: Product) {
			self.product = product

			@Dependency(ProductsService.self) var products
			self.isProductAvailable = products.peekIsAvailable(product)
		}

		public mutating func presentPaywall() -> Effect<Paywall.Action> {
			isPaywallPresented = true
			return .none
		}
	}

	public enum Action: FeatureAction, ViewAction, BindableAction {
		@CasePathable
		public enum View {
			case onAppear
			case didStartTask
			case didTapRestorePurchasesButton
		}
		@CasePathable
		public enum Delegate { case doNothing }
		@CasePathable
		public enum Internal {
			case setProductAvailability(Bool)
			case didFinishRestoringPurchases(Result<Bool, Error>)

			case errors(Errors<ErrorID>.Action)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
		case binding(BindingAction<State>)
	}

	public enum ErrorID: Hashable {
		case failedToRestorePurchases
	}

	public init() {}

	@Dependency(\.dismiss) var dismiss
	@Dependency(ProductsService.self) var products

	public var body: some ReducerOf<Self> {
		BindingReducer()

		Scope(state: \.errors, action: \.internal.errors) {
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
						for try await isPurchased in products.observe(product) {
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

				case .errors(.delegate(.doNothing)), .errors(.internal), .errors(.view):
					return .none
				}

			case .delegate, .binding:
				return .none
			}
		}

		BreadcrumbReducer<State, Action> { _, action in
			switch action {
			case .view(.onAppear): return .navigationBreadcrumb(type(of: self))
			default: return nil
			}
		}

		ErrorHandlerReducer<State, Action> { _, action in
			switch action {
			case let .internal(.didFinishRestoringPurchases(.failure(error))):
				return error
			default:
				return nil
			}
		}
	}
}
