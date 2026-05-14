package zentry.back.api.global;
//IA
import zentry.back.api.ai.dtos.*;
import zentry.back.api.ai.models.*;

//Business
import zentry.back.api.business.dtos.*;
import zentry.back.api.business.models.*;

//core
import zentry.back.api.core.dtos.*;
import zentry.back.api.core.models.*;

public final class mappers {
    private mappers() {}

    //#region IA

    public static iaModelsResponse toResponse(IaModels entity) {
        if (entity == null) return null;
        return iaModelsResponse.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .build();
    }

    public static CommunityEmbeddingsResponse toResponse(CommunityEmbeddings entity) {
        if (entity == null) return null;
        return CommunityEmbeddingsResponse.builder()
                .id(entity.getId())
                .communityId(entity.getCommunityId())
                .vector(entity.getVector())
                .build();
    }

    public static ContentEmbeddingsResponse toResponse(ContentEmbeddings entity) {
        if (entity == null) return null;
        return ContentEmbeddingsResponse.builder()
                .id(entity.getId())
                .postId(entity.getPostId())
                .vector(entity.getVector())
                .build();
    }

    public static FeedbackLabelsResponse toResponse(FeedbackLabels entity) {
        if (entity == null) return null;
        return FeedbackLabelsResponse.builder()
                .id(entity.getId())
                .feedbackId(entity.getFeedbackId())
                .etiqueta(entity.getEtiqueta())
                .build();
    }

    public static IaBehaivorAnalitycsResponse toResponse(IaBehaivorAnalitycs entity) {
        if (entity == null) return null;
        return IaBehaivorAnalitycsResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .score(entity.getScore())
                .build();
    }

    public static IaClustersResponse toResponse(IaClusters entity) {
        if (entity == null) return null;
        return IaClustersResponse.builder()
                .id(entity.getId())
                .descripcion(entity.getDescripcion())
                .build();
    }

    public static IaConfigsResponse toResponse(IaConfigs entity) {
        if (entity == null) return null;
        return IaConfigsResponse.builder()
                .id(entity.getId())
                .modelId(entity.getModelId())
                .parametros(entity.getParametros())
                .build();
    }

    public static IaContentGenerationResponse toResponse(IaContentGeneration entity) {
        if (entity == null) return null;
        return IaContentGenerationResponse.builder()
                .id(entity.getId())
                .promptId(entity.getPromptId())
                .resultado(entity.getResultado())
                .build();
    }

    public static IaFeedbackResponse toResponse(IaFeedback entity) {
        if (entity == null) return null;
        return IaFeedbackResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .comentario(entity.getComentario())
                .build();
    }

    public static IaModerationResultsResponse toResponse(IaModerationResults entity) {
        if (entity == null) return null;
        return IaModerationResultsResponse.builder()
                .id(entity.getId())
                .postId(entity.getPostId())
                .resultado(entity.getResultado())
                .build();
    }

    public static IaPredictionsResponse toResponse(IaPredictions entity) {
        if (entity == null) return null;
        return IaPredictionsResponse.builder()
                .id(entity.getId())
                .modelId(entity.getModelId())
                .resultado(entity.getResultado())
                .build();
    }

    public static IaPromptsResponse toResponse(IaPrompts entity) {
        if (entity == null) return null;
        return IaPromptsResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .prompt(entity.getPrompt())
                .build();
    }

    public static IaRecommendationsResponse toResponse(IaRecommendations entity) {
        if (entity == null) return null;
        return IaRecommendationsResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .contenidoId(entity.getContenidoId())
                .score(entity.getScore())
                .build();
    }

    public static IaScoresResponse toResponse(IaScores entity) {
        if (entity == null) return null;
        return IaScoresResponse.builder()
                .id(entity.getId())
                .postId(entity.getPostId())
                .score(entity.getScore())
                .build();
    }

    public static IaSimilarityContentResponse toResponse(IaSimilarityContent entity) {
        if (entity == null) return null;
        return IaSimilarityContentResponse.builder()
                .id(entity.getId())
                .postId1(entity.getPostId1())
                .postId2(entity.getPostId2())
                .score(entity.getScore())
                .build();
    }

    public static IaSimilarityUsersResponse toResponse(IaSimilarityUsers entity) {
        if (entity == null) return null;
        return IaSimilarityUsersResponse.builder()
                .user1(entity.getUser1())
                .user2(entity.getUser2())
                .score(entity.getScore())
                .build();
    }

    public static IaTrainingDataResponse toResponse(IaTrainingData entity) {
        if (entity == null) return null;
        return IaTrainingDataResponse.builder()
                .id(entity.getId())
                .dataInput(entity.getDataInput())
                .dataOutput(entity.getDataOutput())
                .build();
    }

    public static IaTrainingLogsResponse toResponse(IaTrainingLogs entity) {
        if (entity == null) return null;
        return IaTrainingLogsResponse.builder()
                .id(entity.getId())
                .modelId(entity.getModelId())
                .estado(entity.getEstado())
                .fecha(entity.getFecha())
                .build();
    }

    public static IaVersionsResponse toResponse(IaVersions entity) {
        if (entity == null) return null;
        return IaVersionsResponse.builder()
                .id(entity.getId())
                .modelId(entity.getModelId())
                .version(entity.getVersion())
                .build();
    }

    public static PredictionHistoryResponse toResponse(PredictionHistory entity) {
        if (entity == null) return null;
        return PredictionHistoryResponse.builder()
                .id(entity.getId())
                .predictionId(entity.getPredictionId())
                .fecha(entity.getFecha())
                .build();
    }

    public static RecommendationLogsResponse toResponse(RecommendationLogs entity) {
        if (entity == null) return null;
        return RecommendationLogsResponse.builder()
                .id(entity.getId())
                .recommendationId(entity.getRecommendationId())
                .timestamp(entity.getTimestamp())
                .build();
    }

    public static TagEmbeddingsResponse toResponse(TagEmbeddings entity) {
        if (entity == null) return null;
        return TagEmbeddingsResponse.builder()
                .id(entity.getId())
                .tagId(entity.getTagId())
                .vector(entity.getVector())
                .build();
    }

    public static UserEmbeddingsResponse toResponse(UserEmbeddings entity) {
        if (entity == null) return null;
        return UserEmbeddingsResponse.builder()
                .userId(entity.getUserId())
                .vector(entity.getVector())
                .build();
    }

    //#endregion

    //#region Business

    public static WalletsResponse toResponse(Wallets entity) {
        if (entity == null) return null;
        return WalletsResponse.builder()
                .userId(entity.getUserId())
                .balance(entity.getBalance())
                .build();
    }

    public static CartResponse toResponse(Cart entity) {
        if (entity == null) return null;
        return CartResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .build();
    }

    public static CartItemsResponse toResponse(CartItems entity) {
        if (entity == null) return null;
        return CartItemsResponse.builder()
                .cartId(entity.getCartId())
                .productId(entity.getProductId())
                .cantidad(entity.getCantidad())
                .build();
    }

    public static SubscriptionsResponse toResponse(Subscriptions entity) {
        if (entity == null) return null;
        return SubscriptionsResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .planId(entity.getPlanId())
                .build();
    }

    public static SubscriptionPlansResponse toResponse(SubscriptionPlans entity) {
        if (entity == null) return null;
        return SubscriptionPlansResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .precio(entity.getPrecio())
                .build();
    }

    public static PaymentMethodsResponse toResponse(PaymentMethods entity) {
        if (entity == null) return null;
        return PaymentMethodsResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .metodo(entity.getMetodo())
                .build();
    }

    public static PaymentsResponse toResponse(Payments entity) {
        if (entity == null) return null;
        return PaymentsResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .paymentMethodId(entity.getPaymentMethodId())
                .amount(entity.getAmount())
                .build();
    }

    public static TransactionsResponse toResponse(Transactions entity) {
        if (entity == null) return null;
        return TransactionsResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .amount(entity.getAmount())
                .build();
    }

    public static WalletTransactionsResponse toResponse(WalletTransactions entity) {
        if (entity == null) return null;
        return WalletTransactionsResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .amount(entity.getAmount())
                .build();
    }

    public static InvoicesResponse toResponse(Invoices entity) {
        if (entity == null) return null;
        return InvoicesResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .total(entity.getTotal())
                .build();
    }

    public static InvoicesItemsResponse toResponse(InvoicesItems entity) {
        if (entity == null) return null;
        return InvoicesItemsResponse.builder()
                .id(entity.getId())
                .invoiceId(entity.getInvoiceId())
                .descripcion(entity.getDescripcion())
                .precio(entity.getPrecio())
                .build();
    }

    public static MarketplaceOrdersResponse toResponse(MarketplaceOrders entity) {
        if (entity == null) return null;
        return MarketplaceOrdersResponse.builder()
                .id(entity.getId())
                .buyerId(entity.getBuyerId())
                .total(entity.getTotal())
                .build();
    }

    public static MarketplaceProductsResponse toResponse(MarketplaceProducts entity) {
        if (entity == null) return null;
        return MarketplaceProductsResponse.builder()
                .id(entity.getId())
                .sellerId(entity.getSellerId())
                .nombre(entity.getNombre())
                .precio(entity.getPrecio())
                .build();
    }

    public static OrderItemsResponse toResponse(OrderItems entity) {
        if (entity == null) return null;
        return OrderItemsResponse.builder()
                .orderId(entity.getOrderId())
                .productId(entity.getProductId())
                .cantidad(entity.getCantidad())
                .build();
    }

    public static PayoutsResponse toResponse(Payouts entity) {
        if (entity == null) return null;
        return PayoutsResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .amount(entity.getAmount())
                .build();
    }

    public static PayoutRequestsResponse toResponse(PayoutRequests entity) {
        if (entity == null) return null;
        return PayoutRequestsResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .amount(entity.getAmount())
                .build();
    }

    public static CommissionsResponse toResponse(Commissions entity) {
        if (entity == null) return null;
        return CommissionsResponse.builder()
                .id(entity.getId())
                .porcentaje(entity.getPorcentaje())
                .build();
    }

    public static CommissionJobsResponse toResponse(CommissionJobs entity) {
        if (entity == null) return null;
        return CommissionJobsResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .descripcion(entity.getDescripcion())
                .build();
    }

    public static AffiliateProgramResponse toResponse(AffiliateProgram entity) {
        if (entity == null) return null;
        return AffiliateProgramResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .build();
    }

    public static AffiliatePayoutsResponse toResponse(AffiliatePayouts entity) {
        if (entity == null) return null;
        return AffiliatePayoutsResponse.builder()
                .id(entity.getId())
                .affiliateId(entity.getAffiliateId())
                .amount(entity.getAmount())
                .build();
    }

    public static DonationsResponse toResponse(Donations entity) {
        if (entity == null) return null;
        return DonationsResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .amount(entity.getAmount())
                .build();
    }

    public static ContractsResponse toResponse(Contracts entity) {
        if (entity == null) return null;
        return ContractsResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .detalles(entity.getDetalles())
                .build();
    }

    public static AdsCampaignsResponse toResponse(AdsCampaigns entity) {
        if (entity == null) return null;
        return AdsCampaignsResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .nombre(entity.getNombre())
                .build();
    }

    public static AdImpressionsResponse toResponse(AdImpressions entity) {
        if (entity == null) return null;
        return AdImpressionsResponse.builder()
                .id(entity.getId())
                .campaignId(entity.getCampaignId())
                .vistas(entity.getVistas())
                .build();
    }

    //#endregion

    //#region Core

    public static UserResponse toResponse(User entity) {
        if (entity == null) return null;
        return UserResponse.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .build();
    }

    public static ProfileResponse toResponse(Profile entity) {
        if (entity == null) return null;
        return ProfileResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .bio(entity.getBio())
                .build();
    }

    public static PostResponse toResponse(Post entity) {
        if (entity == null) return null;
        return PostResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .contenido(entity.getContenido())
                .build();
    }

    public static CommentResponse toResponse(Comment entity) {
        if (entity == null) return null;
        return CommentResponse.builder()
                .id(entity.getId())
                .postId(entity.getPostId())
                .userId(entity.getUserId())
                .build();
    }

    public static CommentReactionResponse toResponse(CommentReaction entity) {
        if (entity == null) return null;
        return CommentReactionResponse.builder()
                .id(entity.getId())
                .commentId(entity.getCommentId())
                .userId(entity.getUserId())
                .build();
    }

    public static CommentReplyResponse toResponse(CommentReply entity) {
        if (entity == null) return null;
        return CommentReplyResponse.builder()
                .id(entity.getId())
                .commentId(entity.getCommentId())
                .build();
    }

    public static CommunityResponse toResponse(Community entity) {
        if (entity == null) return null;
        return CommunityResponse.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .build();
    }

    public static CommunityMemberResponse toResponse(CommunityMember entity) {
        if (entity == null) return null;
        return CommunityMemberResponse.builder()
                .communityId(entity.getCommunityId())
                .userId(entity.getUserId())
                .build();
    }

    public static FollowResponse toResponse(Follow entity) {
        if (entity == null) return null;
        return FollowResponse.builder()
                .follower(entity.getFollower())
                .following(entity.getFollowing())
                .build();
    }

    public static FriendshipResponse toResponse(Friendship entity) {
        if (entity == null) return null;
        return FriendshipResponse.builder()
                .user1(entity.getUser1())
                .user2(entity.getUser2())
                .build();
    }

    public static FriendRequestResponse toResponse(FriendRequest entity) {
        if (entity == null) return null;
        return FriendRequestResponse.builder()
                .id(entity.getId())
                .user1(entity.getUser1())
                .user2(entity.getUser2())
                .build();
    }

    public static BlockResponse toResponse(Block entity) {
        if (entity == null) return null;
        return BlockResponse.builder()
                .userId(entity.getUserId())
                .blockedId(entity.getBlockedId())
                .build();
    }

    public static BookmarkResponse toResponse(Bookmark entity) {
        if (entity == null) return null;
        return BookmarkResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .postId(entity.getPostId())
                .build();
    }

    public static CategoryResponse toResponse(Category entity) {
        if (entity == null) return null;
        return CategoryResponse.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .build();
    }

    public static SubcategoryResponse toResponse(Subcategory entity) {
        if (entity == null) return null;
        return SubcategoryResponse.builder()
                .id(entity.getId())
                .categoriaId(entity.getCategoriaId())
                .build();
    }

    public static TagResponse toResponse(Tag entity) {
        if (entity == null) return null;
        return TagResponse.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .build();
    }

    public static PostTagResponse toResponse(PostTag entity) {
        if (entity == null) return null;
        return PostTagResponse.builder()
                .postId(entity.getPostId())
                .tagId(entity.getTagId())
                .build();
    }

    public static PostMediaResponse toResponse(PostMedia entity) {
        if (entity == null) return null;
        return PostMediaResponse.builder()
                .id(entity.getId())
                .postId(entity.getPostId())
                .url(entity.getUrl())
                .build();
    }

    public static PostVersionResponse toResponse(PostVersion entity) {
        if (entity == null) return null;
        return PostVersionResponse.builder()
                .id(entity.getId())
                .postId(entity.getPostId())
                .contenido(entity.getContenido())
                .build();
    }

    public static MediaResponse toResponse(Media entity) {
        if (entity == null) return null;
        return MediaResponse.builder()
                .id(entity.getId())
                .postId(entity.getPostId())
                .url(entity.getUrl())
                .build();
    }

    public static MentionResponse toResponse(Mention entity) {
        if (entity == null) return null;
        return MentionResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .build();
    }

    public static NotificationResponse toResponse(Notification entity) {
        if (entity == null) return null;
        return NotificationResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .build();
    }

    public static ReactionResponse toResponse(Reaction entity) {
        if (entity == null) return null;
        return ReactionResponse.builder()
                .id(entity.getId())
                .postId(entity.getPostId())
                .userId(entity.getUserId())
                .build();
    }

    public static ReportResponse toResponse(Report entity) {
        if (entity == null) return null;
        return ReportResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .build();
    }

    public static ShareResponse toResponse(Share entity) {
        if (entity == null) return null;
        return ShareResponse.builder()
                .id(entity.getId())
                .postId(entity.getPostId())
                .userId(entity.getUserId())
                .build();
    }

    public static SeriesResponse toResponse(Series entity) {
        if (entity == null) return null;
        return SeriesResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .build();
    }

    public static SeriesChapterResponse toResponse(SeriesChapter entity) {
        if (entity == null) return null;
        return SeriesChapterResponse.builder()
                .id(entity.getId())
                .seriesId(entity.getSeriesId())
                .build();
    }

    public static AuditLogResponse toResponse(AuditLog entity) {
        if (entity == null) return null;
        return AuditLogResponse.builder()
                .id(entity.getId())
                .accion(entity.getAccion())
                .build();
    }

    public static FeatureFlagResponse toResponse(FeatureFlag entity) {
        if (entity == null) return null;
        return FeatureFlagResponse.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .build();
    }

    public static SystemConfigResponse toResponse(SystemConfig entity) {
        if (entity == null) return null;
        return SystemConfigResponse.builder()
                .id(entity.getId())
                .clave(entity.getClave())
                .build();
    }

    public static LoginHistoryResponse toResponse(LoginHistory entity) {
        if (entity == null) return null;
        return LoginHistoryResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .ip(entity.getIp())
                .build();
    }

    public static UserDeviceResponse toResponse(UserDevice entity) {
        if (entity == null) return null;
        return UserDeviceResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .dispositivo(entity.getDispositivo())
                .build();
    }

    public static UserSessionResponse toResponse(UserSession entity) {
        if (entity == null) return null;
        return UserSessionResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .token(entity.getToken())
                .build();
    }

    public static UserPreferencesResponse toResponse(UserPreferences entity) {
        if (entity == null) return null;
        return UserPreferencesResponse.builder()
                .userId(entity.getUserId())
                .config(entity.getConfig())
                .build();
    }

    public static UserPrivacyResponse toResponse(UserPrivacy entity) {
        if (entity == null) return null;
        return UserPrivacyResponse.builder()
                .userId(entity.getUserId())
                .nivel(entity.getNivel())
                .build();
    }

    public static UserSettingsResponse toResponse(UserSettings entity) {
        if (entity == null) return null;
        return UserSettingsResponse.builder()
                .userId(entity.getUserId())
                .privacidad(entity.getPrivacidad())
                .build();
    }

    public static ForumThreadResponse toResponse(ForumThread entity) {
        if (entity == null) return null;
        return ForumThreadResponse.builder()
                .id(entity.getId())
                .communityId(entity.getCommunityId())
                .build();
    }

    public static ForumReplyResponse toResponse(ForumReply entity) {
        if (entity == null) return null;
        return ForumReplyResponse.builder()
                .id(entity.getId())
                .threadId(entity.getThreadId())
                .build();
    }

    //#endregion
}


